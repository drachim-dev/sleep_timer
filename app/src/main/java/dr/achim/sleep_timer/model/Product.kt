package dr.achim.sleep_timer.model

enum class Product(val id: String, val isConsumable: Boolean) {
    Donation("donate", isConsumable = true),
    Pro("pro", isConsumable = false);
}

enum class Entitlement(val id: String) {
    Pro("Comfy Sleep Timer Pro")
}