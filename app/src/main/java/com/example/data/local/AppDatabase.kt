package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.data.model.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
    entities = [
        Product::class,
        CartItem::class,
        DeliveryZone::class,
        Order::class,
        InstallationRequest::class,
        MarketingEvent::class,
        AppSettings::class
    ],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun productDao(): ProductDao
    abstract fun cartDao(): CartDao
    abstract fun orderDao(): OrderDao
    abstract fun deliveryZoneDao(): DeliveryZoneDao
    abstract fun installationDao(): InstallationDao
    abstract fun marketingDao(): MarketingDao
    abstract fun settingsDao(): SettingsDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "gason_database.db"
                )
                    .addCallback(object : RoomDatabase.Callback() {
                        override fun onCreate(db: SupportSQLiteDatabase) {
                            super.onCreate(db)
                            INSTANCE?.let { database ->
                                CoroutineScope(Dispatchers.IO).launch {
                                    populateInitialData(database)
                                }
                            }
                        }
                    })
                    .build()
                INSTANCE = instance
                instance
            }
        }

        suspend fun populateInitialData(db: AppDatabase) {
            // Seed Delivery Zones
            val initialZones = listOf(
                DeliveryZone(
                    id = "zone_henley",
                    name = "Henley on Klip",
                    deliveryFee = 50.0,
                    freeDeliveryThreshold = 900.0,
                    minimumOrder = 200.0,
                    estimatedDeliveryTime = "30 - 60 mins"
                ),
                DeliveryZone(
                    id = "zone_meyerton",
                    name = "Meyerton",
                    deliveryFee = 50.0,
                    freeDeliveryThreshold = 900.0,
                    minimumOrder = 200.0,
                    estimatedDeliveryTime = "30 - 60 mins"
                ),
                DeliveryZone(
                    id = "zone_midvaal",
                    name = "Midvaal",
                    deliveryFee = 70.0,
                    freeDeliveryThreshold = 1000.0,
                    minimumOrder = 250.0,
                    estimatedDeliveryTime = "45 - 90 mins"
                ),
                DeliveryZone(
                    id = "zone_surround",
                    name = "Surrounding Areas",
                    deliveryFee = 90.0,
                    freeDeliveryThreshold = 1200.0,
                    minimumOrder = 300.0,
                    estimatedDeliveryTime = "Within 2 - 3 hours"
                )
            )
            db.deliveryZoneDao().insertZones(initialZones)

            // Seed App Settings
            db.settingsDao().saveSettings(AppSettings())

            // Seed Products
            val products = listOf(
                Product(
                    id = "gas_3kg",
                    name = "3kg LP Gas Cylinder",
                    sku = "GO-CYL-3KG",
                    category = "GAS BOTTLES",
                    description = "Compact LP gas cylinder, ideal for camping, single plate burners and portable lamps.",
                    price = 180.0,
                    newBottlePrice = 480.0,
                    stock = 35,
                    bottleSize = "3 kg",
                    weightKg = 3.0,
                    specifications = "Weight: 3kg LPG | Thread: 3/8 BSP | Use: Camping & small appliances",
                    isFeatured = false,
                    tagline = "Camping & Portable",
                    imageType = "CYLINDER_BLUE"
                ),
                Product(
                    id = "gas_5kg",
                    name = "5kg LP Gas Cylinder",
                    sku = "GO-CYL-5KG",
                    category = "GAS BOTTLES",
                    description = "Popular medium cylinder for 2-burner portable stoves, skottels, and outdoor patio cookers.",
                    price = 260.0,
                    newBottlePrice = 620.0,
                    stock = 40,
                    bottleSize = "5 kg",
                    weightKg = 5.0,
                    specifications = "Weight: 5kg LPG | Valve: Domestic 3/8 | Safe SABS certified",
                    isFeatured = false,
                    tagline = "Outdoor & Skottels",
                    imageType = "CYLINDER_BLUE"
                ),
                Product(
                    id = "gas_9kg",
                    name = "9kg LP Gas Cylinder",
                    sku = "GO-CYL-9KG",
                    category = "GAS BOTTLES",
                    description = "Most popular household bottle. Perfect for indoor gas heaters, gas braais, stoves and hobs.",
                    price = 499.0,
                    newBottlePrice = 1099.0,
                    salePrice = 479.0,
                    stock = 85,
                    bottleSize = "9 kg",
                    weightKg = 9.0,
                    specifications = "Weight: 9kg LPG | Valve: 5/8 Bullnose | Fits standard rollabout heaters & braais",
                    isFeatured = true,
                    isSpecialOffer = true,
                    tagline = "Ideal for stoves, ovens & braais",
                    imageType = "CYLINDER_BLUE"
                ),
                Product(
                    id = "gas_11kg",
                    name = "11kg LP Gas Cylinder",
                    sku = "GO-CYL-11KG",
                    category = "GAS BOTTLES",
                    description = "High capacity domestic bottle suitable for intensive cooking or large patio heaters.",
                    price = 599.0,
                    newBottlePrice = 1250.0,
                    stock = 25,
                    bottleSize = "11 kg",
                    weightKg = 11.0,
                    specifications = "Weight: 11kg LPG | Bullnose fitting | Heavy duty steel construction",
                    isFeatured = false,
                    tagline = "Extended household use",
                    imageType = "CYLINDER_SILVER"
                ),
                Product(
                    id = "gas_14kg",
                    name = "14kg LP Gas Cylinder",
                    sku = "GO-CYL-14KG",
                    category = "GAS BOTTLES",
                    description = "Excellent capacity for medium households with gas cookers and small geysers.",
                    price = 699.0,
                    newBottlePrice = 1450.0,
                    stock = 30,
                    bottleSize = "14 kg",
                    weightKg = 14.0,
                    specifications = "Weight: 14kg LPG | Standard bullnose connection | SANS approved",
                    isFeatured = false,
                    tagline = "Cooking & Small Geysers",
                    imageType = "CYLINDER_SILVER"
                ),
                Product(
                    id = "gas_19kg",
                    name = "19kg LP Gas Cylinder",
                    sku = "GO-CYL-19KG",
                    category = "GAS BOTTLES",
                    description = "Heavy domestic and small commercial cylinder. Great for gas geysers, whole home cooking and guesthouses.",
                    price = 899.0,
                    newBottlePrice = 1899.0,
                    stock = 55,
                    bottleSize = "19 kg",
                    weightKg = 19.0,
                    specifications = "Weight: 19kg LPG | High flow bullnose | Ideal for gas geysers & backup power",
                    isFeatured = true,
                    tagline = "Great for geysers & larger appliances",
                    imageType = "CYLINDER_SILVER"
                ),
                Product(
                    id = "gas_48kg",
                    name = "48kg LP Gas Cylinder",
                    sku = "GO-CYL-48KG",
                    category = "GAS BOTTLES",
                    description = "Large commercial/industrial gas bottle for restaurants, bakeries, workshops and whole-estate manifold systems.",
                    price = 2199.0,
                    newBottlePrice = 4200.0,
                    stock = 20,
                    bottleSize = "48 kg",
                    weightKg = 48.0,
                    specifications = "Weight: 48kg LPG | Dual valve available | Manifold compatible | Heavy duty",
                    isFeatured = true,
                    tagline = "For businesses & high usage",
                    imageType = "CYLINDER_GREEN"
                ),
                // Domestic Appliances
                Product(
                    id = "app_geyser_12l",
                    name = "12L Outdoor Gas Geyser",
                    sku = "GO-APP-GEY12",
                    category = "GEYSERS",
                    description = "Instant water heater for continuous hot water without loadshedding stress. Weather-proof outdoor model with digital temperature display.",
                    price = 4299.0,
                    salePrice = 3999.0,
                    stock = 12,
                    specifications = "Flow rate: 12L/min | Ignition: Automatic battery | Gas: LPG | Stainless steel cover",
                    isFeatured = true,
                    isSpecialOffer = true,
                    tagline = "Never run out of hot water",
                    imageType = "GEYSER"
                ),
                Product(
                    id = "app_hob_2b",
                    name = "2 Burner Gas Hob Stainless",
                    sku = "GO-APP-HOB2B",
                    category = "GAS STOVES",
                    description = "Sleek built-in stainless steel gas hob with cast iron pan supports and flame failure safety devices.",
                    price = 1499.0,
                    stock = 18,
                    specifications = "Burners: 2 (1 Rapid, 1 Semi-rapid) | Material: Brushed Stainless Steel | Flame failure cut-off",
                    isFeatured = true,
                    tagline = "Sleek loadshedding-proof cooking",
                    imageType = "HOB"
                ),
                Product(
                    id = "app_heater_15mj",
                    name = "15MJ Convection Space Heater",
                    sku = "GO-APP-HTR15",
                    category = "HEATERS",
                    description = "High efficiency convection gas heater with 3 ceramic heat panels, oxygen depletion sensor (ODS) and tilt switch.",
                    price = 2899.0,
                    salePrice = 2699.0,
                    stock = 15,
                    specifications = "Output: 15MJ (4.2kW) | Safety: ODS & Tilt switch | Fits 9kg cylinder internally",
                    isFeatured = true,
                    tagline = "Instant cozy winter warmth",
                    imageType = "HEATER"
                ),
                Product(
                    id = "app_braai_3b",
                    name = "3 Burner Stainless Steel Gas Braai",
                    sku = "GO-APP-BRAAI3",
                    category = "BRAAIS",
                    description = "Premium stainless steel gas braai with side burner, thermometer lid, cast iron grates, and storage cabinet.",
                    price = 4899.0,
                    stock = 8,
                    specifications = "Burners: 3 + 1 Side Burner | Stainless Steel 430 | Piezo electronic ignition",
                    isFeatured = false,
                    tagline = "The ultimate South African braai",
                    imageType = "GRILLER"
                ),
                // Commercial Appliances
                Product(
                    id = "comm_griller_floor",
                    name = "Floor Model Gas Griller",
                    sku = "GO-COM-GRL01",
                    category = "COMMERCIAL APPLIANCES",
                    description = "Heavy duty commercial lava rock gas griller on sturdy stainless steel floor stand with drip tray.",
                    price = 8999.0,
                    stock = 5,
                    specifications = "Size: 600mm | Burners: 4 Cast Iron | Heavy duty pilot light & piezo ignition",
                    isFeatured = true,
                    tagline = "High performance restaurant grilling",
                    imageType = "GRILLER"
                ),
                Product(
                    id = "comm_fryer_double",
                    name = "Gas Double Deep Fryer 2x10L",
                    sku = "GO-COM-FRY20",
                    category = "COMMERCIAL APPLIANCES",
                    description = "Double well commercial gas fryer with individual temperature controls, baskets and cold zone.",
                    price = 7899.0,
                    stock = 6,
                    specifications = "Capacity: 2 x 10 Litre | Fast temperature recovery | Drain taps included",
                    isFeatured = false,
                    tagline = "Catering grade deep frying",
                    imageType = "GRILLER"
                ),
                Product(
                    id = "comm_cooker_4b",
                    name = "Commercial Gas Cooker 4-Burner",
                    sku = "GO-COM-CKR4B",
                    category = "COMMERCIAL APPLIANCES",
                    description = "Industrial 4-burner boiling table with individual needle valves and heavy cast iron pot supports.",
                    price = 4599.0,
                    stock = 10,
                    specifications = "Burners: 4 Vortex High Output | Construction: Angle iron powder coated | LPG high pressure",
                    isFeatured = false,
                    tagline = "For food stalls, caterers & butcheries",
                    imageType = "HOB"
                ),
                // Gas Supplies & Fittings
                Product(
                    id = "acc_regulator_gauge",
                    name = "LPG Low Pressure Regulator with Gauge",
                    sku = "GO-ACC-REG01",
                    category = "REGULATORS",
                    description = "Domestic low pressure regulator (2.8kPa) with built-in contents gauge indicator and excess flow valve.",
                    price = 280.0,
                    stock = 60,
                    specifications = "Inlet: 5/8 Bullnose | Outlet: 8mm Hose nozzle | Pressure: 2.8 kPa | SANS 1237",
                    isFeatured = false,
                    tagline = "Essential safety regulator with gauge",
                    imageType = "REGULATOR"
                ),
                Product(
                    id = "acc_gas_hose",
                    name = "2m Reinforced LPG Hose with Clamps",
                    sku = "GO-ACC-HSE02",
                    category = "HOSES",
                    description = "High grade orange rubber LPG hose complete with 2 stainless steel jubilee worm-drive hose clamps.",
                    price = 120.0,
                    stock = 100,
                    specifications = "Length: 2 Metres | Internal Dia: 8mm | SANS approved orange reinforced rubber",
                    isFeatured = false,
                    tagline = "Certified gas hose",
                    imageType = "REGULATOR"
                ),
                Product(
                    id = "acc_bottle_stand",
                    name = "Anti-Theft Gas Bottle Cage Stand",
                    sku = "GO-ACC-STND1",
                    category = "ACCESSORIES",
                    description = "Lockable galvanized steel wall cage stand for 9kg or 19kg gas bottles. Meets municipal safety requirements.",
                    price = 650.0,
                    stock = 25,
                    specifications = "Fits: 9kg & 19kg bottles | Padlock hasp | Pre-drilled wall mounting holes",
                    isFeatured = false,
                    tagline = "Secure outdoor bottle protection",
                    imageType = "CYLINDER_SILVER"
                )
            )
            db.productDao().insertProducts(products)

            // Seed sample orders for demo tracking
            val demoOrder1 = Order(
                orderNumber = "GO-2026-00001",
                customerName = "Johan van der Merwe",
                customerMobile = "0829876543",
                customerEmail = "johan@example.co.za",
                customerWhatsApp = "0829876543",
                isDelivery = true,
                deliveryAddress = "45 River Road, Henley on Klip",
                deliverySuburb = "Henley on Klip",
                deliveryCity = "Meyerton",
                deliveryPostalCode = "1962",
                deliveryZoneId = "zone_henley",
                deliveryInstructions = "Please ring gate bell at white wooden gate.",
                scheduleType = "ASAP",
                paymentMethod = "DIRECT_EFT",
                paymentStatus = "CONFIRMED",
                orderStatus = "OUT FOR DELIVERY",
                itemsSummary = "1x 9kg LP Gas Cylinder (Exchange), 1x LPG Regulator with Gauge",
                subtotal = 779.0,
                deliveryFee = 50.0,
                discountAmount = 0.0,
                total = 829.0,
                createdAt = System.currentTimeMillis() - 3600000
            )

            val demoOrder2 = Order(
                orderNumber = "GO-2026-00002",
                customerName = "Sarah Ndlovu",
                customerMobile = "0715551234",
                customerEmail = "sarah.ndlovu@example.co.za",
                customerWhatsApp = "0715551234",
                isDelivery = false,
                deliveryAddress = "",
                deliverySuburb = "",
                deliveryCity = "",
                deliveryPostalCode = "",
                deliveryZoneId = "",
                deliveryInstructions = "",
                collectionSlot = "Today, 14:00 - 16:00",
                scheduleType = "SCHEDULED",
                paymentMethod = "PAYFAST",
                paymentStatus = "CONFIRMED",
                orderStatus = "READY FOR COLLECTION",
                itemsSummary = "1x 19kg LP Gas Cylinder (New Bottle)",
                subtotal = 1899.0,
                deliveryFee = 0.0,
                discountAmount = 0.0,
                total = 1899.0,
                createdAt = System.currentTimeMillis() - 7200000
            )

            db.orderDao().insertOrder(demoOrder1)
            db.orderDao().insertOrder(demoOrder2)
        }
    }
}
