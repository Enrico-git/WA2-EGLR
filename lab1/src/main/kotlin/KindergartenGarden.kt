class KindergartenGarden(private  val diagram: String) {
    val students = arrayListOf("alice", "bob", "charlie", "david", "eve", "fred", "ginny", "harriet", "ileana", "joseph", "kincaid", "larry")

    fun getPlantsOfStudent(student: String): List<String>{
        var result = mutableListOf<String>()
        /*val index = students.indexOf(student)
        if(index != -1){
            var tmp = ""
            var index1 = index*2
            tmp += diagram[index1]
            index1 += 1
            tmp += diagram[index1]
            result.add(0, tmp)
            tmp = ""
            index1 = (index1 - 1)  + 25
            tmp += diagram[index1]
            index1 += 1
            tmp += diagram[index1]
            result.add(1, tmp)
        }*/
        if(students.contains(student.toLowerCase())){
            var index = students.indexOf(student.toLowerCase())
            index *= 2
            diagram.split("\n").
                    forEach {
                        result.add(it[index].toString())
                        result.add(it[index+1].toString())
                    }

        }
        return result
    }
}

fun main(){
    val diagram = "VRCGGCRVVRCGGCRVVRCGGCRV\nVCVGGRRCVCVGGRRCVCVGGRRC"
    val garden = KindergartenGarden(diagram)
    println(garden.getPlantsOfStudent("Charlie"))
}

