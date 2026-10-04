package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "products")
data class Product(
    @PrimaryKey val id: String,
    val name: String,
    val sku: String,
    val category: String,
    val description: String,
    val price: Double, // Exchange price or regular item price
    val newBottlePrice: Double? = null, // If cylinder: price for full brand new bottle
    val salePrice: Double? = null,
    val stock: Int = 50,
    val bottleSize: String? = null, // "3 kg", "5 kg", "9 kg", "11 kg", "14 kg", "19 kg", "48 kg"
    val weightKg: Double = 9.0,
    val specifications: String = "",
    val isDeliveryAvailable: Boolean = true,
    val isFeatured: Boolean = false,
    val isActive: Boolean = true,
    val isSpecialOffer: Boolean = false,
    val tagline: String = "",
    val imageType: String = "CYLINDER_BLUE" // CYLINDER_BLUE, CYLINDER_SILVER, CYLINDER_GREEN, GEYSER, HOB, HEATER, GRILLER, REGULATOR
)

@Entity(tableName = "cart_items")
data class CartItem(
    @PrimaryKey val id: String, // productId + (ifExchange ? "_ex" : "_new")
    val productId: String,
    val productName: String,
    val price: Double,
    val quantity: Int,
    val isExchange: Boolean = true,
    val bottleSize: String? = null,
    val category: String = "",
    val imageType: String = "CYLINDER_BLUE"
) {
    val subtotal: Double
        get() = price * quantity
}

@Entity(tableName = "delivery_zones")
data class DeliveryZone(
    @PrimaryKey val id: String,
    val name: String,
    val deliveryFee: Double,
    val freeDeliveryThreshold: Double,
    val minimumOrder: Double,
    val estimatedDeliveryTime: String,
    val isActive: Boolean = true
)

@Entity(tableName = "orders")
data class Order(
    @PrimaryKey val orderNumber: String,
    val customerName: String,
    val customerMobile: String,
    val customerEmail: String,
    val customerWhatsApp: String,
    val isDelivery: Boolean,
    val deliveryAddress: String,
    val deliverySuburb: String,
    val deliveryCity: String,
    val deliveryPostalCode: String,
    val deliveryZoneId: String,
    val deliveryInstructions: String,
    val collectionSlot: String = "",
    val scheduleType: String = "ASAP", // "ASAP" or "SCHEDULED"
    val scheduledDate: String = "",
    val scheduledTimeWindow: String = "",
    val paymentMethod: String, // "DIRECT_EFT", "PAYFAST", "PAYPAL", "WHATSAPP"
    val paymentStatus: String, // "PENDING_EFT", "CONFIRMED", "PENDING"
    val orderStatus: String, // "ORDER RECEIVED", "PAYMENT CONFIRMED", "ORDER PROCESSING", "OUT FOR DELIVERY", "READY FOR COLLECTION", "DELIVERED", "COMPLETED"
    val itemsSummary: String, // Human readable items summary
    val subtotal: Double,
    val deliveryFee: Double,
    val discountAmount: Double,
    val total: Double,
    val createdAt: Long = System.currentTimeMillis(),
    val proofOfPaymentNote: String? = null
)

@Entity(tableName = "installation_requests")
data class InstallationRequest(
    @PrimaryKey val id: String,
    val customerName: String,
    val customerPhone: String,
    val customerEmail: String,
    val address: String,
    val installationType: String,
    val preferredDate: String,
    val message: String,
    val hasPhotoAttached: Boolean = false,
    val status: String = "Pending Review",
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "marketing_events")
data class MarketingEvent(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val eventName: String,
    val details: String,
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "app_settings")
data class AppSettings(
    @PrimaryKey val id: Int = 1,
    val whatsAppNumber: String = "+27821234567",
    val contactPhone: String = "016 362 5555",
    val contactEmail: String = "info@gason.co.za",
    val depotAddress: String = "12 Loch Street, Meyerton, Midvaal, 1961",
    val bankName: String = "Standard Bank South Africa",
    val bankAccountName: String = "GasOn (Pty) Ltd",
    val bankAccountNumber: String = "1019 4839 2810",
    val bankBranchCode: String = "051001",
    val isDemoMode: Boolean = true,
    val payFastMerchantId: String = "10000100 (Demo Mode)",
    val payFastMerchantKey: String = "46f0cd694581a (Demo Mode)",
    val payPalClientId: String = "demo_paypal_client_id_placeholder"
)
