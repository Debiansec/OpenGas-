package com.example.data.repository

import com.example.data.local.AppDatabase
import com.example.data.model.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlin.random.Random

class GasStoreRepository(private val db: AppDatabase) {

    val activeProducts: Flow<List<Product>> = db.productDao().getAllActiveProducts()
    val allProductsAdmin: Flow<List<Product>> = db.productDao().getAllProductsAdmin()
    val gasBottles: Flow<List<Product>> = db.productDao().getGasBottles()
    val cartItems: Flow<List<CartItem>> = db.cartDao().getCartItems()
    val deliveryZones: Flow<List<DeliveryZone>> = db.deliveryZoneDao().getAllDeliveryZones()
    val allOrders: Flow<List<Order>> = db.orderDao().getAllOrders()
    val installationRequests: Flow<List<InstallationRequest>> = db.installationDao().getAllRequests()
    val appSettings: Flow<AppSettings?> = db.settingsDao().getSettings()
    val marketingEvents: Flow<List<MarketingEvent>> = db.marketingDao().getEvents()

    suspend fun getProductById(id: String): Product? = db.productDao().getProductById(id)

    suspend fun addToCart(product: Product, isExchange: Boolean, quantity: Int = 1) {
        val unitPrice = if (isExchange) product.price else (product.newBottlePrice ?: product.price)
        val cartItemId = if (product.bottleSize != null) {
            "${product.id}_${if (isExchange) "ex" else "new"}"
        } else {
            product.id
        }

        val nameSuffix = if (product.bottleSize != null) {
            if (isExchange) " (Exchange Bottle)" else " (New Bottle + Deposit)"
        } else ""

        val existing = db.cartDao().getCartItems().first().find { it.id == cartItemId }
        val newQuantity = (existing?.quantity ?: 0) + quantity

        val item = CartItem(
            id = cartItemId,
            productId = product.id,
            productName = "${product.name}$nameSuffix",
            price = unitPrice,
            quantity = newQuantity,
            isExchange = isExchange,
            bottleSize = product.bottleSize,
            category = product.category,
            imageType = product.imageType
        )
        db.cartDao().insertOrUpdateItem(item)
        trackMarketingEvent("add_to_cart", "Product: ${product.name}, Qty: $quantity, Price: R$unitPrice")
    }

    suspend fun updateCartItemQuantity(cartItemId: String, newQuantity: Int) {
        if (newQuantity <= 0) {
            db.cartDao().removeItem(cartItemId)
        } else {
            val items = db.cartDao().getCartItems().first()
            val item = items.find { it.id == cartItemId }
            if (item != null) {
                db.cartDao().insertOrUpdateItem(item.copy(quantity = newQuantity))
            }
        }
    }

    suspend fun removeCartItem(cartItemId: String) {
        db.cartDao().removeItem(cartItemId)
    }

    suspend fun clearCart() {
        db.cartDao().clearCart()
    }

    suspend fun createOrder(
        customerName: String,
        customerMobile: String,
        customerEmail: String,
        customerWhatsApp: String,
        isDelivery: Boolean,
        deliveryAddress: String,
        deliverySuburb: String,
        deliveryCity: String,
        deliveryPostalCode: String,
        deliveryZoneId: String,
        deliveryInstructions: String,
        collectionSlot: String,
        scheduleType: String,
        scheduledDate: String,
        scheduledTimeWindow: String,
        paymentMethod: String,
        subtotal: Double,
        deliveryFee: Double,
        discountAmount: Double,
        itemsSummary: String,
        proofOfPaymentNote: String? = null
    ): Order {
        val randomNum = Random.nextInt(1000, 9999)
        val orderNumber = "GO-2026-$randomNum"
        val total = (subtotal + deliveryFee - discountAmount).coerceAtLeast(0.0)

        val paymentStatus = when (paymentMethod) {
            "DIRECT_EFT" -> "PENDING_EFT"
            "PAYFAST" -> "CONFIRMED"
            "PAYPAL" -> "CONFIRMED"
            else -> "PENDING"
        }

        val initialOrderStatus = if (paymentMethod == "DIRECT_EFT") "ORDER RECEIVED" else "PAYMENT CONFIRMED"

        val order = Order(
            orderNumber = orderNumber,
            customerName = customerName,
            customerMobile = customerMobile,
            customerEmail = customerEmail,
            customerWhatsApp = customerWhatsApp,
            isDelivery = isDelivery,
            deliveryAddress = deliveryAddress,
            deliverySuburb = deliverySuburb,
            deliveryCity = deliveryCity,
            deliveryPostalCode = deliveryPostalCode,
            deliveryZoneId = deliveryZoneId,
            deliveryInstructions = deliveryInstructions,
            collectionSlot = collectionSlot,
            scheduleType = scheduleType,
            scheduledDate = scheduledDate,
            scheduledTimeWindow = scheduledTimeWindow,
            paymentMethod = paymentMethod,
            paymentStatus = paymentStatus,
            orderStatus = initialOrderStatus,
            itemsSummary = itemsSummary,
            subtotal = subtotal,
            deliveryFee = deliveryFee,
            discountAmount = discountAmount,
            total = total,
            proofOfPaymentNote = proofOfPaymentNote
        )

        db.orderDao().insertOrder(order)
        clearCart()
        trackMarketingEvent("purchase", "Order: $orderNumber, Total: R$total, Method: $paymentMethod")
        return order
    }

    suspend fun getOrderByNumber(orderNumber: String): Order? {
        return db.orderDao().getOrderByNumber(orderNumber.trim())
    }

    suspend fun updateOrderStatus(orderNumber: String, status: String) {
        db.orderDao().updateOrderStatus(orderNumber, status)
    }

    suspend fun updatePaymentStatus(orderNumber: String, status: String) {
        db.orderDao().updatePaymentStatus(orderNumber, status)
    }

    suspend fun submitInstallationRequest(request: InstallationRequest) {
        db.installationDao().insertRequest(request)
        trackMarketingEvent("installation_request", "Type: ${request.installationType}, Customer: ${request.customerName}")
    }

    suspend fun updateProduct(product: Product) {
        db.productDao().updateProduct(product)
    }

    suspend fun insertProduct(product: Product) {
        db.productDao().insertProduct(product)
    }

    suspend fun deleteProduct(productId: String) {
        db.productDao().deleteProduct(productId)
    }

    suspend fun updateDeliveryZone(zone: DeliveryZone) {
        db.deliveryZoneDao().updateZone(zone)
    }

    suspend fun saveSettings(settings: AppSettings) {
        db.settingsDao().saveSettings(settings)
    }

    suspend fun trackMarketingEvent(eventName: String, details: String) {
        db.marketingDao().trackEvent(MarketingEvent(eventName = eventName, details = details))
    }
}
