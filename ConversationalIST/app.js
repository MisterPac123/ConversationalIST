const express = require('express')
const app = express()
const mongoClient = require('mongodb').MongoClient

const url = "mongodb://localhost:27017"

app.use(express.json())

mongoClient.connect(url, (err, db) => {

    if (err) {
        console.log("Error while connecting mongo client")
    } else {

        const myDb = db.db('myDb')
        const collection = myDb.collection('myTable')

        app.post('/signup', (req, res) =>{
            
            const newUser = {
                email: req.body.email,
                name: req.body.name,
                username: req.body.username,
                password: req.body.password
            }

	    console.log("signup")

            const query = { email: newUser.email }

            collection.findOne(query, (err, result) => {

                if(result == null){
		    console.log("signup sucess")
                    collection.insertOne(newUser, (err, result) =>{
                        res.status(200).send()
                    })
                } else {
		    console.log("signup failed")
                    res.status(400).send()
                }
            })

        })
	
        app.post('/login', (req, res) =>{

            const query = { username: req.body.username,
                            password: req.body.password}

	    console.log("Login")

            collection.findOne(query, (err, result) => {

                if (result != null) {

                    const objToSend = {
                        name: result.name,
                        email: result.email
                    }
                    
		    console.log("Login success")
                    res.status(200).send(JSON.stringify(objToSend))

                } else {
		    console.log("Login fail")
                    res.status(404).send()
                }

            })

        })
    }
})

app.listen(3000, () => {
    console.log("Listening on port 3000...")
})