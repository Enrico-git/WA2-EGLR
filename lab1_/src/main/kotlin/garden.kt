class KindergartenGarden(private val diagram: String) {

    private val _student = arrayListOf("alice", "bob", "charlie" , "david", "eve", "fred",
        "ginny", "harriet", "ileana", "joseph", "kincaid", "larry")

    fun getPlantsOfStudent(student: String): List<String> {
        println("Student: $student")
        val results = mutableListOf<String>()
        if (!_student.contains(student.toLowerCase()))
            return results

        val index = _student.indexOf(student.toLowerCase())*2

        diagram
            .split("\n")
            .forEach{
            results.add(it[index].toString())
            results.add(it[index+1].toString())
        }

        return results
    }
}

fun main(){
    val kgg = KindergartenGarden("VRCGVVRVCGGCCGVRGCVCGCGV\nVRCCCGCRRGVCGCRVVCVGCGCV")
    println("Diagram: VRCGVVRVCGGCCGVRGCVCGCGV\\nVRCCCGCRRGVCGCRVVCVGCGCV")
    println(kgg.getPlantsOfStudent("Alice"))
    println(kgg.getPlantsOfStudent("Bob"))
    println(kgg.getPlantsOfStudent("charlie"))
    println(kgg.getPlantsOfStudent("larry"))
    println(kgg.getPlantsOfStudent("Enrico"))
}