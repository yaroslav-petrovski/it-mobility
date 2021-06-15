package mumayank.com.itmobilityproject

/**
 * Result item
 *
 * Class to store the shop information from the database
 *
 * @property Adress adress of the shop
 * @property cntProducts number of searched product
 * @property lat latitude (shop position)
 * @property long longitude (shop position)
 * @property distanceToUser distance between user and shop (calculated in Result activity)
 */
class ResultItem(
    val Adress: String,
    val cntProducts: Int,
    val lat: Double,
    val long: Double,
    val distanceToUser: Float
) {


}
