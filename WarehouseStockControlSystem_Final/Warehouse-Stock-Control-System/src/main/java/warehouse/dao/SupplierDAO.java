package warehouse.dao;

import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import warehouse.interfaces.Crud;
import warehouse.model.Supplier;
import warehouse.util.DatabaseException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

@Repository
public class SupplierDAO extends JdbcDao implements Crud<Supplier> {
    public SupplierDAO(JdbcTemplate jdbc) { super(jdbc); }
    @Override public Supplier save(Supplier supplier) {
        try { supplier.setSupplierId(insertAndGetId("INSERT INTO supplier (name, contact_info) VALUES (?,?)",supplier.getName(),supplier.getContactInfo())); return supplier; }
        catch(DataAccessException e){throw databaseError("Could not save supplier. Its name may already exist.",e);}
    }
    @Override public Optional<Supplier> findById(int id) { return one("SELECT supplier_id,name,contact_info FROM supplier WHERE supplier_id=?",id); }
    @Override public List<Supplier> findAll() { try{return jdbc.query("SELECT supplier_id,name,contact_info FROM supplier ORDER BY name",(rs,row)->map(rs));}catch(DataAccessException e){throw databaseError("Could not load suppliers.",e);} }
    @Override public boolean update(Supplier supplier) { try{return jdbc.update("UPDATE supplier SET name=?,contact_info=? WHERE supplier_id=?",supplier.getName(),supplier.getContactInfo(),supplier.getSupplierId())==1;}catch(DataAccessException e){throw databaseError("Could not update supplier.",e);} }
    @Override public boolean delete(int id) { try{return jdbc.update("DELETE FROM supplier WHERE supplier_id=?",id)==1;}catch(DataAccessException e){throw databaseError("Cannot delete a supplier used by stock-movement history.",e);} }
    private Optional<Supplier> one(String sql,int id){try{return jdbc.query(sql,rs->rs.next()?Optional.of(map(rs)):Optional.empty(),id);}catch(DataAccessException e){throw databaseError("Could not look up supplier.",e);}}
    private Supplier map(ResultSet r)throws SQLException{return new Supplier(r.getInt("supplier_id"),r.getString("name"),r.getString("contact_info"));}
}
