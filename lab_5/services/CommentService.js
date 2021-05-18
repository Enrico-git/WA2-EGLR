'use strict'

import Comments from '../models/Comments.js'

export let getCommentsById = (comments) => {
    return Comments.find({ _id : { $in : comments } } ).exec()
}