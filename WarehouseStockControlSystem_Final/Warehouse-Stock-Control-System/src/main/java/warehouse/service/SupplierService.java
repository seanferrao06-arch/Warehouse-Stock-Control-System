package warehouse.service;

import org.springframework.stereotype.Service;
import warehouse.dao.SupplierDAO;
import warehouse.model.Supplier;
import warehouse.util.DatabaseException;

import java.util.List;

@Service
public class SupplierService {
    private final SupplierDAO suppliers;
    public SupplierService(SupplierDAO suppliers) { this.suppliers = suppliers; }
    public List<Supplier> all() { return suppliers.findAll(); }
    public Supplier one(int id) { return suppliers.findById(id).orElseThrow(() -> new DatabaseException("Supplier not found.")); }
    public Supplier create(Supplier supplier) { return suppliers.save(supplier); }
    public void update(Supplier supplier) { if (!suppliers.update(supplier)) throw new DatabaseException("Supplier not found."); }
    public void delete(int id) { if (!suppliers.delete(id)) throw new DatabaseException("Supplier not found."); }
}
