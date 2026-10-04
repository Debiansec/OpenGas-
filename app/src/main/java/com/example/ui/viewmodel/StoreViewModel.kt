package com.example.ui.viewmodel

import android.app.Application
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.AppDatabase
import com.example.data.model.*
import com.example.data.repository.GasStoreRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.net.URLEncoder

enum class Screen {
    HOME,
    SHOP,
    GAS_DELIVERY,
    CART,
    CHECKOUT,
    ORDER_TRACKING,
    SAFETY,
    INSTALLATION,
    ACCOUNT,
    ADMIN
}

data class ChatMessage(
    val id: String = java.util.UUID.randomUUID().toString(),
    val text: String,
    val isUser: Boolean,
    val timestamp: Long = System.currentTimeMillis(),
    val actionType: String? = null, // "ADD_9KG", "VIEW_CART", "CHECKOUT", "WHATSAPP", "TRACK_ORDER"
    val actionPayload: String? = null
)

class StoreViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: GasStoreRepository

    init {
        val db = AppDatabase.getDatabase(application)
        repository = GasStoreRepository(db)
    }

    // Splash screen state
    private val _showSplash = MutableStateFlow(true)
    val showSplash: StateFlow<Boolean> = _showSplash.asStateFlow()

    fun dismissSplash() {
        _showSplash.value = false
    }

    fun openSplash() {
        _showSplash.value = true
    }

    // Screens and Navigation
    private val _currentScreen = MutableStateFlow(Screen.HOME)
    val currentScreen: StateFlow<Screen> = _currentScreen.asStateFlow()

    private val _selectedProduct = MutableStateFlow<Product?>(null)
    val selectedProduct: StateFlow<Product?> = _selectedProduct.asStateFlow()

    private val _selectedInvoiceOrder = MutableStateFlow<Order?>(null)
    val selectedInvoiceOrder: StateFlow<Order?> = _selectedInvoiceOrder.asStateFlow()

    // Database flows
    val products: StateFlow<List<Product>> = repository.activeProducts
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val adminProducts: StateFlow<List<Product>> = repository.allProductsAdmin
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val gasBottles: StateFlow<List<Product>> = repository.gasBottles
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val cartItems: StateFlow<List<CartItem>> = repository.cartItems
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val cartSubtotal: StateFlow<Double> = repository.cartItems
        .map { items -> items.sumOf { it.subtotal } }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0.0)

    val cartItemCount: StateFlow<Int> = repository.cartItems
        .map { items -> items.sumOf { it.quantity } }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    val deliveryZones: StateFlow<List<DeliveryZone>> = repository.deliveryZones
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val orders: StateFlow<List<Order>> = repository.allOrders
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val installationRequests: StateFlow<List<InstallationRequest>> = repository.installationRequests
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val appSettings: StateFlow<AppSettings> = repository.appSettings
        .filterNotNull()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), AppSettings())

    val marketingEvents: StateFlow<List<MarketingEvent>> = repository.marketingEvents
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Shop filters
    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _selectedCategory = MutableStateFlow("ALL")
    val selectedCategory: StateFlow<String> = _selectedCategory.asStateFlow()

    private val _selectedBottleSizeFilter = MutableStateFlow("ALL")
    val selectedBottleSizeFilter: StateFlow<String> = _selectedBottleSizeFilter.asStateFlow()

    // Cart calculations & Coupon
    private val _couponCode = MutableStateFlow("")
    val couponCode: StateFlow<String> = _couponCode.asStateFlow()

    private val _couponDiscountPercent = MutableStateFlow(0)
    val couponDiscountPercent: StateFlow<Int> = _couponDiscountPercent.asStateFlow()

    private val _isFreeDeliveryCoupon = MutableStateFlow(false)
    val isFreeDeliveryCoupon: StateFlow<Boolean> = _isFreeDeliveryCoupon.asStateFlow()

    // Checkout form state
    val checkoutStep = MutableStateFlow(1)
    val customerName = MutableStateFlow("Johan van der Merwe")
    val customerMobile = MutableStateFlow("082 123 4567")
    val customerEmail = MutableStateFlow("johan@gason.co.za")
    val customerWhatsApp = MutableStateFlow("082 123 4567")

    val isDeliveryMethod = MutableStateFlow(true) // true = Delivery, false = Collection
    val deliveryAddress = MutableStateFlow("42 Riverbank Avenue")
    val deliverySuburb = MutableStateFlow("Henley on Klip")
    val deliveryCity = MutableStateFlow("Meyerton / Midvaal")
    val deliveryPostalCode = MutableStateFlow("1962")
    val selectedDeliveryZoneId = MutableStateFlow("zone_henley")
    val deliveryInstructions = MutableStateFlow("Please gate buzzer: 42 or phone on arrival")

    val collectionDate = MutableStateFlow("Today")
    val collectionTimeSlot = MutableStateFlow("14:00 - 16:00")

    val scheduleType = MutableStateFlow("ASAP") // "ASAP" or "SCHEDULED"
    val scheduledDate = MutableStateFlow("Tomorrow")
    val scheduledTimeSlot = MutableStateFlow("09:00 - 12:00")

    val selectedPaymentMethod = MutableStateFlow("DIRECT_EFT") // "DIRECT_EFT", "PAYFAST", "PAYPAL"
    val proofOfPaymentUploaded = MutableStateFlow(false)
    val proofOfPaymentFileName = MutableStateFlow("")

    // Tracking search
    val trackingSearchQuery = MutableStateFlow("")
    val trackedOrder = MutableStateFlow<Order?>(null)

    // AI Assistant state
    private val _isAiChatOpen = MutableStateFlow(false)
    val isAiChatOpen: StateFlow<Boolean> = _isAiChatOpen.asStateFlow()

    private val _chatMessages = MutableStateFlow<List<ChatMessage>>(
        listOf(
            ChatMessage(
                text = "Hello! I'm your GasOn AI Assistant. How can I help you today? I can help you choose the right cylinder size, check delivery to Meyerton & Henley on Klip, or add products directly to your cart!",
                isUser = false,
                actionType = "ADD_9KG",
                actionPayload = "Add 9kg Gas Bottle (R499)"
            )
        )
    )
    val chatMessages: StateFlow<List<ChatMessage>> = _chatMessages.asStateFlow()

    fun navigateTo(screen: Screen) {
        _currentScreen.value = screen
        repository.apply {
            viewModelScope.launch {
                trackMarketingEvent("page_view", "Navigated to: ${screen.name}")
            }
        }
    }

    fun openProductDetail(product: Product) {
        _selectedProduct.value = product
        viewModelScope.launch {
            repository.trackMarketingEvent("product_view", "Product: ${product.name} (R${product.price})")
        }
    }

    fun closeProductDetail() {
        _selectedProduct.value = null
    }

    fun openInvoice(order: Order) {
        _selectedInvoiceOrder.value = order
    }

    fun closeInvoice() {
        _selectedInvoiceOrder.value = null
    }

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun setSelectedCategory(category: String) {
        _selectedCategory.value = category
    }

    fun setSelectedBottleSizeFilter(size: String) {
        _selectedBottleSizeFilter.value = size
    }

    fun addToCart(product: Product, isExchange: Boolean, quantity: Int = 1) {
        viewModelScope.launch {
            repository.addToCart(product, isExchange, quantity)
        }
    }

    fun updateCartQuantity(cartItemId: String, newQty: Int) {
        viewModelScope.launch {
            repository.updateCartItemQuantity(cartItemId, newQty)
        }
    }

    fun removeFromCart(cartItemId: String) {
        viewModelScope.launch {
            repository.removeCartItem(cartItemId)
        }
    }

    fun removeProductFromCart(productId: String) {
        viewModelScope.launch {
            val matchingItems = cartItems.value.filter { it.productId == productId }
            matchingItems.forEach { repository.removeCartItem(it.id) }
        }
    }

    fun decrementProductQuantity(productId: String) {
        viewModelScope.launch {
            val matchingItem = cartItems.value.firstOrNull { it.productId == productId }
            if (matchingItem != null) {
                if (matchingItem.quantity > 1) {
                    repository.updateCartItemQuantity(matchingItem.id, matchingItem.quantity - 1)
                } else {
                    repository.removeCartItem(matchingItem.id)
                }
            }
        }
    }

    fun applyCoupon(code: String): String {
        val clean = code.trim().uppercase()
        return when (clean) {
            "WELCOME10" -> {
                _couponCode.value = clean
                _couponDiscountPercent.value = 10
                _isFreeDeliveryCoupon.value = false
                "Coupon WELCOME10 applied! 10% discount on order."
            }
            "FREEGASDEL" -> {
                _couponCode.value = clean
                _couponDiscountPercent.value = 0
                _isFreeDeliveryCoupon.value = true
                "Coupon FREEGASDEL applied! Free delivery."
            }
            "GASON15" -> {
                _couponCode.value = clean
                _couponDiscountPercent.value = 15
                _isFreeDeliveryCoupon.value = false
                "Coupon GASON15 applied! 15% discount."
            }
            else -> {
                "Invalid coupon code. Try WELCOME10 or FREEGASDEL."
            }
        }
    }

    fun removeCoupon() {
        _couponCode.value = ""
        _couponDiscountPercent.value = 0
        _isFreeDeliveryCoupon.value = false
    }

    fun calculateDeliveryFee(subtotal: Double): Double {
        if (!isDeliveryMethod.value) return 0.0
        if (_isFreeDeliveryCoupon.value) return 0.0

        val currentZone = deliveryZones.value.find { it.id == selectedDeliveryZoneId.value }
            ?: deliveryZones.value.firstOrNull()

        if (currentZone != null && subtotal >= currentZone.freeDeliveryThreshold) {
            return 0.0
        }
        return currentZone?.deliveryFee ?: 50.0
    }

    fun submitOrder(onSuccess: (Order) -> Unit) {
        viewModelScope.launch {
            val items = cartItems.value
            if (items.isEmpty()) return@launch

            val subtotal = items.sumOf { it.subtotal }
            val deliveryFee = calculateDeliveryFee(subtotal)
            val discount = (subtotal * (_couponDiscountPercent.value / 100.0))
            val itemsSummary = items.joinToString(", ") { "${it.quantity}x ${it.productName}" }

            val proofNote = if (selectedPaymentMethod.value == "DIRECT_EFT" && proofOfPaymentUploaded.value) {
                "Proof of payment attached: ${proofOfPaymentFileName.value.ifBlank { "EFT_Receipt_POP.pdf" }}"
            } else null

            val order = repository.createOrder(
                customerName = customerName.value,
                customerMobile = customerMobile.value,
                customerEmail = customerEmail.value,
                customerWhatsApp = customerWhatsApp.value,
                isDelivery = isDeliveryMethod.value,
                deliveryAddress = deliveryAddress.value,
                deliverySuburb = deliverySuburb.value,
                deliveryCity = deliveryCity.value,
                deliveryPostalCode = deliveryPostalCode.value,
                deliveryZoneId = selectedDeliveryZoneId.value,
                deliveryInstructions = deliveryInstructions.value,
                collectionSlot = if (!isDeliveryMethod.value) "${collectionDate.value}, ${collectionTimeSlot.value}" else "",
                scheduleType = scheduleType.value,
                scheduledDate = scheduledDate.value,
                scheduledTimeWindow = scheduledTimeSlot.value,
                paymentMethod = selectedPaymentMethod.value,
                subtotal = subtotal,
                deliveryFee = deliveryFee,
                discountAmount = discount,
                itemsSummary = itemsSummary,
                proofOfPaymentNote = proofNote
            )

            // Reset checkout step
            checkoutStep.value = 1
            proofOfPaymentUploaded.value = false
            trackedOrder.value = order
            onSuccess(order)
        }
    }

    private val _orderSearchError = MutableStateFlow<String?>(null)
    val orderSearchError: StateFlow<String?> = _orderSearchError.asStateFlow()

    fun searchOrder(orderId: String, phoneNumber: String = "") {
        viewModelScope.launch {
            val cleanId = orderId.trim()
            val cleanPhone = phoneNumber.trim().replace(" ", "").replace("-", "")
            _orderSearchError.value = null

            if (cleanId.isBlank() && cleanPhone.isBlank()) {
                _orderSearchError.value = "Please enter an Order ID or Phone Number to track."
                return@launch
            }

            val order = orders.value.find { ord ->
                val idMatches = cleanId.isNotBlank() && ord.orderNumber.contains(cleanId, ignoreCase = true)
                val phoneMatches = cleanPhone.isNotBlank() && ord.customerMobile.replace(" ", "").replace("-", "").contains(cleanPhone)
                if (cleanId.isNotBlank() && cleanPhone.isNotBlank()) {
                    idMatches || phoneMatches
                } else if (cleanId.isNotBlank()) {
                    idMatches
                } else {
                    phoneMatches
                }
            } ?: if (cleanId.isNotBlank()) repository.getOrderByNumber(cleanId) else null

            if (order != null) {
                trackedOrder.value = order
                _orderSearchError.value = null
            } else {
                trackedOrder.value = null
                _orderSearchError.value = "No order found for ID \"${cleanId.ifBlank { "N/A" }}\" or Phone \"${cleanPhone.ifBlank { "N/A" }}\"."
            }
        }
    }

    fun searchOrder(query: String) {
        searchOrder(orderId = query, phoneNumber = query)
    }

    fun submitInstallationRequest(
        name: String,
        phone: String,
        email: String,
        address: String,
        type: String,
        date: String,
        message: String,
        hasPhoto: Boolean,
        onSuccess: () -> Unit
    ) {
        viewModelScope.launch {
            val req = InstallationRequest(
                id = "IR-${System.currentTimeMillis() % 100000}",
                customerName = name,
                customerPhone = phone,
                customerEmail = email,
                address = address,
                installationType = type,
                preferredDate = date,
                message = message,
                hasPhotoAttached = hasPhoto
            )
            repository.submitInstallationRequest(req)
            onSuccess()
        }
    }

    // AI Chat Bot
    fun toggleAiChat(open: Boolean? = null) {
        _isAiChatOpen.value = open ?: !_isAiChatOpen.value
    }

    fun sendAiMessage(userText: String) {
        if (userText.isBlank()) return
        val userMsg = ChatMessage(text = userText, isUser = true)
        _chatMessages.value = _chatMessages.value + userMsg

        viewModelScope.launch {
            val reply = generateAiResponse(userText)
            _chatMessages.value = _chatMessages.value + reply
        }
    }

    private suspend fun generateAiResponse(input: String): ChatMessage {
        val lower = input.lowercase()
        return when {
            lower.contains("braai") || lower.contains("heater") || lower.contains("9kg") -> {
                ChatMessage(
                    text = "For a gas braai or domestic room heater, our 9kg LP Gas Cylinder is the standard and most popular choice! It is safe, compact, and fits inside standard rollabout heaters. Refill/Exchange is only R499.",
                    isUser = false,
                    actionType = "ADD_9KG",
                    actionPayload = "Add 9kg Gas Bottle to Cart"
                )
            }
            lower.contains("geyser") || lower.contains("water") || lower.contains("19kg") -> {
                ChatMessage(
                    text = "For a gas geyser or larger household demands, we strongly recommend our 19kg LP Gas Cylinder (R899 exchange) or a manifold setup. It ensures high vaporisation rates even during chilly Midvaal winters!",
                    isUser = false,
                    actionType = "VIEW_PRODUCT",
                    actionPayload = "gas_19kg"
                )
            }
            lower.contains("smell gas") || lower.contains("leak") || lower.contains("safety") || lower.contains("emergency") -> {
                ChatMessage(
                    text = "🚨 IMPORTANT GAS SAFETY:\n1. Extinguish all naked flames immediately.\n2. Do NOT operate electrical switches or phones near the leak.\n3. Turn cylinder valve fully off clockwise.\n4. Open doors & windows for ventilation.\n5. Test connections with soapy water bubbles (never flames!).\n6. For repairs or certification, always contact a qualified gas practitioner.",
                    isUser = false,
                    actionType = "VIEW_SAFETY",
                    actionPayload = "Open Gas Safety Guide"
                )
            }
            lower.contains("area") || lower.contains("deliver") || lower.contains("where") || lower.contains("meyerton") || lower.contains("henley") -> {
                ChatMessage(
                    text = "We proudly deliver across: \n📍 Henley on Klip (30-60 mins)\n📍 Meyerton Central & Suburbs (30-60 mins)\n📍 Midvaal & Highbury (45-90 mins)\n📍 Surrounding agricultural holdings.\nFree delivery is available on qualifying order thresholds!",
                    isUser = false,
                    actionType = "GAS_DELIVERY",
                    actionPayload = "Order LP Gas Now"
                )
            }
            lower.contains("difference") || lower.contains("exchange") || lower.contains("new bottle") -> {
                ChatMessage(
                    text = "• Exchange Bottle: You hand us your empty certified gas cylinder (safe condition), and we give you a fully filled, sealed cylinder. You only pay for the gas.\n• New Bottle: If you don't have an empty cylinder, this includes the steel cylinder deposit + full gas filling.",
                    isUser = false,
                    actionType = "GAS_DELIVERY",
                    actionPayload = "Browse Cylinder Sizes"
                )
            }
            lower.contains("add to cart") || lower.contains("buy 9kg") || lower.contains("order 9kg") -> {
                val p9 = products.value.find { it.id == "gas_9kg" }
                if (p9 != null) {
                    repository.addToCart(p9, isExchange = true, quantity = 1)
                    ChatMessage(
                        text = "Done! I've added a 9kg LP Gas Cylinder (Exchange, R499) directly to your cart. Would you like to view your cart or proceed to checkout?",
                        isUser = false,
                        actionType = "VIEW_CART",
                        actionPayload = "View Shopping Cart"
                    )
                } else {
                    ChatMessage(text = "I've placed the 9kg bottle in your cart for you!", isUser = false)
                }
            }
            lower.contains("cart") || lower.contains("checkout") -> {
                ChatMessage(
                    text = "You can review your items, select Delivery or Collection, and pay securely via Direct EFT, PayFast, or PayPal.",
                    isUser = false,
                    actionType = "CHECKOUT",
                    actionPayload = "Proceed to Checkout"
                )
            }
            else -> {
                ChatMessage(
                    text = "I'm here to help with all GasOn products, deliveries in Henley on Klip and Meyerton, or appliance recommendations. Feel free to ask about bottle sizes, geysers, or installation services!",
                    isUser = false,
                    actionType = "WHATSAPP",
                    actionPayload = "Chat directly on WhatsApp"
                )
            }
        }
    }

    fun handleChatAction(actionType: String?, payload: String?) {
        when (actionType) {
            "ADD_9KG" -> {
                val p9 = products.value.find { it.id == "gas_9kg" }
                if (p9 != null) {
                    addToCart(p9, isExchange = true, quantity = 1)
                    navigateTo(Screen.CART)
                    toggleAiChat(false)
                }
            }
            "VIEW_CART" -> {
                navigateTo(Screen.CART)
                toggleAiChat(false)
            }
            "CHECKOUT" -> {
                navigateTo(Screen.CHECKOUT)
                toggleAiChat(false)
            }
            "GAS_DELIVERY" -> {
                navigateTo(Screen.GAS_DELIVERY)
                toggleAiChat(false)
            }
            "VIEW_SAFETY" -> {
                navigateTo(Screen.SAFETY)
                toggleAiChat(false)
            }
            "WHATSAPP" -> {
                // Open WhatsApp
                toggleAiChat(false)
            }
        }
    }

    // WhatsApp ordering formatted text generator
    fun generateWhatsAppOrderText(order: Order): String {
        return """
            Hello GasOn,

            I would like to place an order.

            Order Number:
            ${order.orderNumber}

            Products:
            ${order.itemsSummary}

            Subtotal:
            R ${String.format("%.2f", order.subtotal)}

            Delivery/Collection:
            ${if (order.isDelivery) "🚚 Delivery to ${order.deliverySuburb}" else "📦 Customer Collection"}

            Delivery Address:
            ${if (order.isDelivery) "${order.deliveryAddress}, ${order.deliverySuburb}, ${order.deliveryCity}" else "Collection at GasOn Depot"}

            Total:
            R ${String.format("%.2f", order.total)}

            Payment Method:
            ${order.paymentMethod}

            Please confirm availability and delivery.
        """.trimIndent()
    }

    fun openWhatsApp(context: Context, customMessage: String? = null) {
        val number = appSettings.value.whatsAppNumber.replace("+", "").replace(" ", "")
        val defaultText = "Hello GasOn, I would like to enquire about LP Gas delivery."
        val textToSend = customMessage ?: defaultText
        val encoded = URLEncoder.encode(textToSend, "UTF-8")
        val uri = Uri.parse("https://wa.me/$number?text=$encoded")
        val intent = Intent(Intent.ACTION_VIEW, uri)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        try {
            context.startActivity(intent)
            viewModelScope.launch {
                repository.trackMarketingEvent("whatsapp_click", "Message sent: ${textToSend.take(30)}...")
            }
        } catch (e: Exception) {
            Toast.makeText(context, "WhatsApp application not installed", Toast.LENGTH_SHORT).show()
        }
    }

    fun makePhoneCall(context: Context) {
        val phone = appSettings.value.contactPhone.replace(" ", "")
        val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:$phone"))
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        try {
            context.startActivity(intent)
            viewModelScope.launch {
                repository.trackMarketingEvent("phone_click", "Call initiated to: $phone")
            }
        } catch (e: Exception) {
            Toast.makeText(context, "Could not launch dialler", Toast.LENGTH_SHORT).show()
        }
    }

    // Admin Operations
    fun adminUpdateOrderStatus(orderNumber: String, newStatus: String) {
        viewModelScope.launch {
            repository.updateOrderStatus(orderNumber, newStatus)
            if (newStatus == "PAYMENT CONFIRMED" || newStatus == "COMPLETED") {
                repository.updatePaymentStatus(orderNumber, "CONFIRMED")
            }
        }
    }

    fun adminConfirmEftPayment(orderNumber: String) {
        viewModelScope.launch {
            repository.updatePaymentStatus(orderNumber, "CONFIRMED")
            repository.updateOrderStatus(orderNumber, "PAYMENT CONFIRMED")
        }
    }

    fun adminUpdateProductPriceAndStock(productId: String, newPrice: Double, newStock: Int) {
        viewModelScope.launch {
            val p = repository.getProductById(productId)
            if (p != null) {
                repository.updateProduct(p.copy(price = newPrice, stock = newStock))
            }
        }
    }

    fun adminToggleProductActive(productId: String, isActive: Boolean) {
        viewModelScope.launch {
            val p = repository.getProductById(productId)
            if (p != null) {
                repository.updateProduct(p.copy(isActive = isActive))
            }
        }
    }

    fun adminSaveSettings(newSettings: AppSettings) {
        viewModelScope.launch {
            repository.saveSettings(newSettings)
        }
    }
}
