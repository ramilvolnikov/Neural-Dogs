import com.google.gson.annotations.SerializedName

//Kotlin dog from server (POJO)
//@SerializedName() - keys from JSON-data

data class ServerDog (

	@SerializedName("breeds") val breeds : List<Breeds>,
	@SerializedName("id") val id : String,
	@SerializedName("url") val url : String,
	@SerializedName("width") val width : Int,
	@SerializedName("height") val height : Int
)
data class Breeds (

	@SerializedName("weight") val weight : Weight,
	@SerializedName("height") val height : Height,
	@SerializedName("id") val id : Int,
	@SerializedName("name") val name : String,
	@SerializedName("bred_for") val bred_for : String,
	@SerializedName("breed_group") val breed_group : String,
	@SerializedName("life_span") val life_span : String,
	@SerializedName("temperament") val temperament : String,
	@SerializedName("origin") val origin : String
)
data class Height (

	@SerializedName("imperial") val imperial : String,
	@SerializedName("metric") val metric : String
)

data class Weight (

	@SerializedName("imperial") val imperial : String,
	@SerializedName("metric") val metric : String
)