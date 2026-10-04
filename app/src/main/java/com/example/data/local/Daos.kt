package com.example.data.local

import androidx.room.*
import com.example.data.model.*
import kotlinx.coroutines.flow.Flow

@Dao
interface ProductDao {
    @Query("SELECT * FROM products WHERE isActive = 1 ORDER BY price ASC")
    fun getAllActiveProducts(): Flow<List<Product>>

    @Query("SELECT * FROM products ORDER BY price ASC")
    fun getAllProductsAdmin(): Flow<List<Product>>

    @Query("SELECT * FROM products WHERE category = 'GAS BOTTLES' AND isActive = 1 ORDER BY weightKg ASC")
    fun getGasBottles(): Flow<List<Product>>

    @Query("SELECT * FROM products WHERE id = :id")
    suspend fun getProductById(id: String): Product?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProducts(products: List<Product>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProduct(product: Product)

    @Update
    suspend fun updateProduct(product: Product)

    @Query("DELETE FROM products WHERE id = :id")
    suspend fun deleteProduct(id: String)
}

@Dao
interface CartDao {
    @Query("SELECT * FROM cart_items")
    fun getCartItems(): Flow<List<CartItem>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdateItem(item: CartItem)

    @Query("DELETE FROM cart_items WHERE id = :id")
    suspend fun removeItem(id: String)

    @Query("DELETE FROM cart_items")
    suspend fun clearCart()
}

@Dao
interface OrderDao {
    @Query("SELECT * FROM orders ORDER BY createdAt DESC")
    fun getAllOrders(): Flow<List<Order>>

    @Query("SELECT * FROM orders WHERE orderNumber = :orderNumber LIMIT 1")
    suspend fun getOrderByNumber(orderNumber: String): Order?

    @Query("SELECT * FROM orders WHERE customerMobile = :mobile ORDER BY createdAt DESC")
    fun getOrdersByCustomer(mobile: String): Flow<List<Order>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrder(order: Order)

    @Update
    suspend fun updateOrder(order: Order)

    @Query("UPDATE orders SET orderStatus = :status WHERE orderNumber = :orderNumber")
    suspend fun updateOrderStatus(orderNumber: String, status: String)

    @Query("UPDATE orders SET paymentStatus = :status WHERE orderNumber = :orderNumber")
    suspend fun updatePaymentStatus(orderNumber: String, status: String)
}

@Dao
interface DeliveryZoneDao {
    @Query("SELECT * FROM delivery_zones")
    fun getAllDeliveryZones(): Flow<List<DeliveryZone>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertZones(zones: List<DeliveryZone>)

    @Update
    suspend fun updateZone(zone: DeliveryZone)
}

@Dao
interface InstallationDao {
    @Query("SELECT * FROM installation_requests ORDER BY createdAt DESC")
    fun getAllRequests(): Flow<List<InstallationRequest>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRequest(request: InstallationRequest)

    @Update
    suspend fun updateRequest(request: InstallationRequest)
}

@Dao
interface MarketingDao {
    @Query("SELECT * FROM marketing_events ORDER BY timestamp DESC LIMIT 100")
    fun getEvents(): Flow<List<MarketingEvent>>

    @Insert
    suspend fun trackEvent(event: MarketingEvent)
}

@Dao
interface SettingsDao {
    @Query("SELECT * FROM app_settings WHERE id = 1 LIMIT 1")
    fun getSettings(): Flow<AppSettings?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveSettings(settings: AppSettings)
}
