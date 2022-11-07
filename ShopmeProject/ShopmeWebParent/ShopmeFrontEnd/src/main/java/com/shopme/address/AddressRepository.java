package com.shopme.address;

import java.util.List;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import com.shopme.common.entity.Address;
import com.shopme.common.entity.Customer;

public interface AddressRepository extends CrudRepository<Address, Integer>{

  public List<Address> findByCustomer(Customer customer);
  
  @Query("SELECT a FROM Address a where a.id =?1 and a.customer.id=?2")
  public Address findByIdAndCustomer(Integer addressId, Integer customerId);
  
  @Query("DELETE FROM Address a WHERE a.id =?1 AND a.customer.id =?2")
  @Modifying
  public void deleteByIdAndCustomer(Integer addressId, Integer customerId);
  
  @Query("UPDATE Address a set a.defaultForShipping =true where a.id = ?1")
  @Modifying
  public void setDefaultaddress(Integer id);
  
  @Query("UPDATE Address a SET a.defaultForShipping = false where a.id != ?1 and a.customer.id = ?2")
  @Modifying
  public void setNonDefaultForShipping(Integer defaultAddressId, Integer customerId);
  
  @Query("SELECT a from Address a where a.customer.id = ?1 and a.defaultForShipping = true")
  public Address findDefaultForCustomer(Integer customerId);
}
