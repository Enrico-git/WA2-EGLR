import mongoose from "mongoose";
import Comment from './comment.js'

const productSchema = new mongoose.Schema( {
    _id: mongoose.ObjectId,
    name: String,
    createdAt: Date,
    description:String,
    price: Number,
    comments: [Comment.schema],
    category: String,
    stars: Number
});

/*
productSchema.findOne({'name': "andonio"}, function(err, product) {
    if(err) //do som
        console.log("andonio wasted");
    else{

    }

});*/

export default mongoose.model('product', productSchema, 'products');