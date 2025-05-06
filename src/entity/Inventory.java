package entity;

import jakarta.persistence.*;
import java.io.Serializable;
import java.util.Objects;

@Entity
@Table(name = "inventory")
@IdClass(Inventory.Pk.class)
public class Inventory {

    @Id
    @Column
    private Integer warehouse_id;

    @Id
    @Column
    private Integer item_id;

    @Column
    private int quantity = 0;

    @Column
    private int reorder_level = 0;

    public Inventory() {}

    public Integer getWarehouseId() {
        return warehouse_id;
    }

    public void setWarehouseId(Integer warehouse_id) {
        this.warehouse_id = warehouse_id;
    }

    public Integer getItemId() {
        return item_id;
    }

    public void setItemId(Integer item_id) {
        this.item_id = item_id;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public int getReorderLevel() {
        return reorder_level;
    }

    public void setReorderLevel(int reorder_level) {
        this.reorder_level = reorder_level;
    }


    @Override
    public String toString() {
        try (org.hibernate.Session session = db.Database.getSessionFactory().openSession()) {
            entity.Item item = session.get(entity.Item.class, item_id);
            entity.Warehouse warehouse = session.get(entity.Warehouse.class, warehouse_id);
            String itemStr = item != null ? item.toString() : "Unknown Item";
            String warehouseStr = warehouse != null ? warehouse.toString() : "Unknown Warehouse";
            return warehouseStr + " - " + itemStr;
        }
    }

    public static class Pk implements Serializable {
        public Integer warehouse_id;
        public Integer item_id;

        public Pk() {
        }

        public Pk(Integer warehouse_id, Integer item_id) {
            this.warehouse_id = warehouse_id;
            this.item_id = item_id;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof Pk)) return false;
            Pk pk = (Pk) o;
            return Objects.equals(warehouse_id, pk.warehouse_id) &&
                    Objects.equals(item_id, pk.item_id);
        }

        @Override
        public int hashCode() {
            return Objects.hash(warehouse_id, item_id);
        }



    }
}
