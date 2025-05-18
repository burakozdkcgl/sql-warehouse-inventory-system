package db;

import entity.Inventory;
import entity.Order;
import entity.OrderLine;
import logic.App;
import logic.Language;
import logic.NotificationPanel;
import org.hibernate.Session;
import org.hibernate.Transaction;

import java.util.HashMap;
import java.util.Map;

public class OrderService {

    public static boolean updateOrderStatus(Order order, String newStatus) {
        try (Session session = Database.getSessionFactory().openSession()) {
            Transaction tx = session.beginTransaction();

            order.setStatus(newStatus);
            session.merge(order);

            if ("Approved".equals(newStatus)) {
                // Step 1: Pre-validation for all order lines
                Map<Inventory.Pk, Integer> required = new HashMap<>();
                for (OrderLine line : order.getOrderLines()) {
                    if (line.getFromWarehouse() != null) {
                        Inventory.Pk key = new Inventory.Pk(line.getFromWarehouse().getId(), line.getItem().getId());
                        required.merge(key, line.getQuantity(), Integer::sum);
                    }
                }

                for (Map.Entry<Inventory.Pk, Integer> entry : required.entrySet()) {
                    Inventory inv = session.get(Inventory.class, entry.getKey());
                    if (inv == null || inv.getQuantity() < entry.getValue()) {
                        tx.rollback();
                        NotificationPanel.show(App.getInstance().getLayeredPane(),
                                Language.get("order.order_could_not_approved"),
                                3000, "red");
                        return false;
                    }
                }

                // Step 2: Apply changes
                for (OrderLine line : order.getOrderLines()) {
                    Integer itemId = line.getItem().getId();
                    Integer fromId = line.getFromWarehouse() != null ? line.getFromWarehouse().getId() : null;
                    Integer toId = line.getToWarehouse() != null ? line.getToWarehouse().getId() : null;
                    int qty = line.getQuantity();

                    if (fromId != null) {
                        Inventory inv = session.get(Inventory.class, new Inventory.Pk(fromId, itemId));
                        inv.setQuantity(inv.getQuantity() - qty);
                        session.merge(inv);
                    }

                    if (toId != null) {
                        Inventory inv = session.get(Inventory.class, new Inventory.Pk(toId, itemId));
                        if (inv == null) {
                            inv = new Inventory();
                            inv.setWarehouseId(toId);
                            inv.setItemId(itemId);
                            inv.setQuantity(qty);
                        } else {
                            inv.setQuantity(inv.getQuantity() + qty);
                        }
                        session.merge(inv);
                    }
                }
            }

            tx.commit();
            NotificationPanel.show(App.getInstance().getLayeredPane(),
                    Language.get("order.order_" + newStatus.toLowerCase()),
                    3000, "green");
            return true;

        } catch (Exception ex) {
            ex.printStackTrace();
            NotificationPanel.show(App.getInstance().getLayeredPane(), "Failed to update order.", 3000, "red");
            return false;
        }
    }
}
