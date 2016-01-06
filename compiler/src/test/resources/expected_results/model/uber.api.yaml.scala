package uber.api
package object yaml {
import de.zalando.play.controllers.ArrayWrapper
import de.zalando.play.controllers.PlayPathBindables
    type EstimatesPriceGetEnd_latitude = Double

    type ProfileLast_name = Option[String]

    type ProductsGetResponses200Opt = ArrayWrapper[Product]

    type EstimatesPriceGetResponses200Opt = ArrayWrapper[PriceEstimate]

    type ActivitiesHistory = Option[ActivitiesHistoryOpt]

    type PriceEstimateLow_estimate = Option[Double]

    type HistoryGetResponsesDefault = Option[Error]

    type HistoryGetResponses200 = Option[Activities]

    type ProductsGetResponses200 = Option[ProductsGetResponses200Opt]

    type MeGetResponses200 = Option[Profile]

    type ActivitiesHistoryOpt = ArrayWrapper[Activity]

    type EstimatesPriceGetResponses200 = Option[EstimatesPriceGetResponses200Opt]

    type ActivitiesLimit = Option[Int]

    case class Activity(uuid: ProfileLast_name) 

    case class PriceEstimate(low_estimate: PriceEstimateLow_estimate, display_name: ProfileLast_name, estimate: ProfileLast_name, high_estimate: PriceEstimateLow_estimate, product_id: ProfileLast_name, currency_code: ProfileLast_name, surge_multiplier: PriceEstimateLow_estimate) 

    case class Product(image: ProfileLast_name, description: ProfileLast_name, display_name: ProfileLast_name, product_id: ProfileLast_name, capacity: ProfileLast_name) 

    case class Profile(first_name: ProfileLast_name, email: ProfileLast_name, promo_code: ProfileLast_name, last_name: ProfileLast_name, picture: ProfileLast_name) 

    case class Activities(offset: ActivitiesLimit, limit: ActivitiesLimit, count: ActivitiesLimit, history: ActivitiesHistory) 

    case class Error(code: ActivitiesLimit, message: ProfileLast_name, fields: ProfileLast_name)

    implicit val bindable_OptionIntQuery = PlayPathBindables.createOptionQueryBindable[Int]
    implicit val bindable_OptionStringQuery = PlayPathBindables.createOptionQueryBindable[String]

}
