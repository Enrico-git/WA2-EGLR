class KindergartenGarden(private val diagram: String) {
    fun getPlantsOfStudent(student: String): List<String> {
        val students = listOf<String>("Alice", "Bob", "Chiarlie", "David", "Eve", "Fred", "Ginny", "Harriet",
            "Ileana", "Joseph", "Kincaid", "Larry")
        val index = students.indexOf(student)
        val rows = diagram.split("\n")
        val plants = mapOf("G" to "GRASS", "C" to "CLOVER", "R" to "RADISHES", "V" to "VIOLETS")
        return rows.flatMap{listOf(it.get(index*2).toString(),it.get(index*2+1).toString())}
            .map{plants.get(it)!!}


    }
}

fun main(){
    val setUp = KindergartenGarden("VRGCGCCCVRCGVRGCGCCCVRCG\nVGCCGGGGVRRRVGCCGGGGVRRR")
    println(setUp.getPlantsOfStudent("Larry"))
}