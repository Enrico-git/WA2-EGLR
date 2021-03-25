class KindergartenGarden(private val diagram: String) {
    private val students = arrayListOf<String>("alice","bob","charlie","david","eve","fred",
                                        "ginny","harriet","ileana","joseph","kincaid","larry")
    fun getPlantsOfStudent(student: String): List<String> {
        var plants = mutableListOf<String>()
        if(!students.contains(student.toLowerCase()))
            return plants
        var index = students.indexOf(student.toLowerCase())*2
        diagram.split("\n")
            .forEach {
                plants.add(it[index].toString())
                plants.add(it[index+1].toString())
            }
        return plants
    }
}

fun main() {
    var diagram = "VRCGGCRVVVRRCRVCCGCVCCRG\nRGVCCGCRRGVCGCRVVCVGCGCV"
    println(diagram)
    var garden = KindergartenGarden(diagram)
    println(garden.getPlantsOfStudent("David"))
}