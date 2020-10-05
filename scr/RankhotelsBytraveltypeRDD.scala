import org.apache.spark
import org.apache.spark.rdd.RDD
import org.apache.spark.sql.types.{DataTypes, StructField, StructType}
import org.apache.spark.sql._
import org.apache.spark.sql.functions._
import org.apache.spark.{SparkConf, SparkContext}

object RankCountiesBySexRDD {

  def main(arg: Array[String]): Unit = {

    val sparkSession = SparkSession.builder
      .master("S3[*]")
      .appName("Example")
      .getOrCreate()

    import sparkSession.implicits._

    var HotelRDD: RDD[Hotel] = sparkSession.sparkContext.textFile("thefoldername/documentname.csv")
      .map(row => Hotel(
        row.substring(18,25), // Logical Record No
        row.substring(226,316).trim, // Name
        row.substring(8,11) // Summary Level (050 is State)
      )).filter(Hotel => Hotel.State == "050")

    val ReservationRDD: RDD[Reservation] = sparkSession.sparkContext.textFile("thefoldername/documentname.csv")
      .map(row => row.split(","))
      .map(csv => Reservation(csv(4), csv(6).toInt, csv(30).toInt))

    // create pair RDDs
    val HotelPair: RDD[(String, Hotel)] = HotelRDD.map(Hotel => (Hotel.HotelName, Hotel.State))
    val popPair: RDD[(String, Reservation)] = reservationRDD.map(p => (p.HotelName, p))

    // join
    val join: RDD[(String, (Hotel, Reservation))] = HotelPair.join(popPair)

    // flatten
    val flatRDD: RDD[HotelTravelType] = join.map((tuple: (String, (TravelType, Hotel)))
        => HotelTravelType(tuple._2._1.name, tuple._2._2.business, tuple._2._2.leisure))

    // show top N
    flatRDD.sortBy((p: HotelTravelType) => p.business*1.0f/p.leisure, ascending = true)
      .zipWithIndex()
      .filter((t: (HotelTravelType, Long)) => t._2 < 10)
      .foreach(println)

  }

}

case class Hotel(HotelName: String, City: String, State: String)

case class Population(HotelName: String, business: Int, leisure: Int)

case class CountyPopulation(State: String, business: Int, leisure: Int)




