console.log("Hello, Node!")

let i = 0

const id = setInterval(() => {
	i++
	console.log("Timer expired $i")
	if (i==3) clearInterval(id)
}, 3000)

// if NOTING to wait for the program quit
