package dr.achim.sleep_timer.model

enum class Product(
    val id: String,
    val isConsumable: Boolean,
    val displayOrder: Int
) {
    Pro(
        id = "pro",
        isConsumable = false,
        displayOrder = 0
    ),
    Donation(
        id = "donate",
        isConsumable = true,
        displayOrder = 1
    );
}

enum class Entitlement(val id: String) {
    Pro("Comfy Sleep Timer Pro")
}