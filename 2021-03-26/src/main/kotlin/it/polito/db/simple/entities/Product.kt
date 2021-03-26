package it.polito.db.simple.entities

import javax.persistence.*

@Entity
class Product(
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    var id: Long?,
    var name: String? ,
    var price: Double? )

//@Entity
//@Table(name="prd")
//class Product{
//    @Id
//    @GeneratedValue(strategy = GenerationType.AUTO)
//    var id: Long? = null
//    //deve essere nullable perchè all'inizio non sò il valore di ID, me lo darà il DB
//
//    @Column(name="n")
//    var name: String? = null
//    var price: Double? = null)
//}

