package dao;

import models.Foodtype;
import models.Restaurant;
import org.sql2o.Connection;
import org.sql2o.Sql2o;
import org.sql2o.Sql2oException;

import java.util.ArrayList;
import java.util.List;

public class Sql2oRestaurantDao implements RestaurantDao {
    private final Sql2o sql2o;

    public Sql2oRestaurantDao(Sql2o sql2o) {
        this.sql2o = sql2o;
    }

    @Override
    public void add(Restaurant restaurant) {
        String sql = "INSERT INTO restaurants (name, address, zipcode, phone, website, email, image) VALUES (:name, :address, :zipcode, :phone, :website, :email, :image)";
        try (Connection con = sql2o.open()) {
            int id = (int) con.createQuery(sql)
                    .addParameter("name", restaurant.getName())
                    .addParameter("address", restaurant.getAddress())
                    .addParameter("zipcode", restaurant.getZipcode())
                    .addParameter("phone", restaurant.getPhone())
                    .addParameter("website", restaurant.getWebsite())
                    .addParameter("email", restaurant.getEmail())
                    .addParameter("image", restaurant.getImage())
                    .addColumnMapping("NAME", "name")
                    .addColumnMapping("ADDRESS", "address")
                    .addColumnMapping("ZIPCODE", "zipcode")
                    .addColumnMapping("PHONE", "phone")
                    .addColumnMapping("WEBSITE", "website")
                    .addColumnMapping("EMAIL", "email")
                    .addColumnMapping("IMAGE", "image")
                    .executeUpdate()
                    .getKey();
            restaurant.setId(id);
        } catch (Sql2oException ex) {
            System.out.println(ex);
        }
    }

    @Override
    public Restaurant findById(int id) {
        try (Connection con = sql2o.open()) {
            return con.createQuery("SELECT * FROM restaurants WHERE id = :id")
                    .addParameter("id", id)
                    .executeAndFetchFirst(Restaurant.class);
        }
    }

    @Override
    public List<Restaurant> getAll() {
        try (Connection con = sql2o.open()) {
            return con.createQuery("SELECT * FROM restaurants")
                    .executeAndFetch(Restaurant.class);
        }
    }

    @Override
    public void update(int id, String newName, String newAddress, String newZip, String newPhone, String newWeb, String newEmail, String newImage) {
        String sql = "UPDATE restaurants SET (name, address, zipcode, phone, website, email, image) = (:name, :address, :zipcode, :phone, :website, :email, :image) WHERE id = :id";
        try (Connection con = sql2o.open()) {
            con.createQuery(sql)
                    .addParameter("id", id)
                    .addParameter("name", newName)
                    .addParameter("address", newAddress)
                    .addParameter("zipcode", newZip)
                    .addParameter("phone", newPhone)
                    .addParameter("website", newWeb)
                    .addParameter("email", newEmail)
                    .addParameter("image", newImage)
                    .executeUpdate();
        } catch (Sql2oException ex) {
            System.out.println(ex);
        }
    }

    @Override
    public void deleteById(int id) {
        String sql = "DELETE from restaurants WHERE id = :id";
        String deleteJoin = "DELETE from restaurants_foodtypes WHERE restaurantid = :restaurantId";
        try (Connection con = sql2o.open()) {
            con.createQuery(sql)
                    .addParameter("id", id)
                    .executeUpdate();
            con.createQuery(deleteJoin)
                    .addParameter("restaurantId", id)
                    .executeUpdate();

        } catch (Sql2oException ex) {
            System.out.println(ex);
        }
    }

    @Override
    public void clearAllRestaurants() {
        String sql = "DELETE from restaurants";
        try (Connection con = sql2o.open()) {
            con.createQuery(sql)
                    .executeUpdate();
        } catch (Sql2oException ex) {
            System.out.println(ex);
        }
    }

    @Override
    public void addRestaurantToFoodtype(Restaurant restaurant, Foodtype foodtype) {
        String sql = "INSERT INTO restaurants_foodtypes (restaurantid, foodtypeid) VALUES (:restaurantId, :foodtypeId)";
        try (Connection con = sql2o.open()) {
            con.createQuery(sql)
                    .addParameter("restaurantId", restaurant.getId())
                    .addParameter("foodtypeId", foodtype.getId())
                    .executeUpdate();
        } catch (Sql2oException ex) {
            System.out.println(ex);
        }
    }

    @Override
    public List<Foodtype> getAllFoodtypesForARestaurant(int restaurantId) {
        List<Foodtype> foodtypes = new ArrayList(); // initialize an empty list
        String joinQuery = "SELECT foodtypeid FROM restaurants_foodtypes WHERE restaurantid = :restaurantId";

        try (Connection con = sql2o.open()) {
            List<Integer> allFoodtypesIds = con.createQuery(joinQuery)
                    .addParameter("restaurantId", restaurantId)
                    .executeAndFetch(Integer.class);
            for (Integer foodId : allFoodtypesIds) {
                String foodtypeQuery = "SELECT * FROM foodtypes WHERE id = :foodtypeId";
                foodtypes.add(
                        con.createQuery(foodtypeQuery)
                                .addParameter("foodtypeId", foodId)
                                .executeAndFetchFirst(Foodtype.class));
            }
        } catch (Sql2oException ex) {
            System.out.println(ex);
        }
        return foodtypes;
    }


}

