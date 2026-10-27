package warehouse.dao;

import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import warehouse.interfaces.Crud;
import warehouse.model.WarehouseLocation;
import warehouse.util.DatabaseException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

@Repository
public class LocationDAO extends JdbcDao implements Crud<WarehouseLocation> {
    public LocationDAO(JdbcTemplate jdbc) { super(jdbc); }
    @Override public WarehouseLocation save(WarehouseLocation location) { try{location.setLocationId(insertAndGetId("INSERT INTO warehouse_location (name) VALUES (?)",location.getName()));return location;}catch(DataAccessException e){throw databaseError("Could not save location. Its name may already exist.",e);} }
    @Override public Optional<WarehouseLocation> findById(int id){return one("SELECT location_id,name FROM warehouse_location WHERE location_id=?",id);}
    @Override public List<WarehouseLocation> findAll(){try{return jdbc.query("SELECT location_id,name FROM warehouse_location ORDER BY name",(rs,row)->map(rs));}catch(DataAccessException e){throw databaseError("Could not load warehouse locations.",e);}}
    @Override public boolean update(WarehouseLocation location){try{return jdbc.update("UPDATE warehouse_location SET name=? WHERE location_id=?",location.getName(),location.getLocationId())==1;}catch(DataAccessException e){throw databaseError("Could not update location.",e);}}
    @Override public boolean delete(int id){try{return jdbc.update("DELETE FROM warehouse_location WHERE location_id=?",id)==1;}catch(DataAccessException e){throw databaseError("Cannot delete a location with stock or movement history.",e);}}
    private Optional<WarehouseLocation> one(String sql,int id){try{return jdbc.query(sql,rs->rs.next()?Optional.of(map(rs)):Optional.empty(),id);}catch(DataAccessException e){throw databaseError("Could not look up location.",e);}}
    private WarehouseLocation map(ResultSet r)throws SQLException{return new WarehouseLocation(r.getInt("location_id"),r.getString("name"));}
}
