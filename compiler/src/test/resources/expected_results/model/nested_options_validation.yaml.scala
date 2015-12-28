package nested_options_validation
package object yaml {
import java.util.Date
import java.io.File

type BasicOptional = Option[BasicOptionalOpt]

    type BasicOptionalNested_optional = Option[String]

    case class BasicOptionalOpt(nested_optional: BasicOptionalNested_optional) 

    case class Basic(optional: BasicOptional) 

    

}
