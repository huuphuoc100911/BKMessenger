var express = require("express");
var app = express();
var server = require("http").createServer(app);
var io = require("socket.io").listen(server);
var fs = require("fs");
var MongoClient = require('mongodb').MongoClient;
var url= "mongodb://localhost:27017/";
server.listen(process.env.PORT || 4000);

var ListRegisterName=[];
var ListRegister= [];
var ListLogin=[];
var ListSocketId= [];
var find_friend= "";
var id_find="";
var id_find_login="";

io.sockets.on('connection', function (socket) {
	
  console.log("Có 1 kết nối mới!");
  var result=false;
  var result_login= false;
  socket.on('ClientRegister',function(data){
	
	 
  	if(ListRegisterName.indexOf(data["username"]) > -1){
  		console.log("This Account was esisted");
  		result=false;
	}else {
		ListRegister.push(data);
		ListSocketId.push(socket.id);
		console.log(ListSocketId);
		ListRegisterName.push(data["username"]);
	
		MongoClient.connect(url, function(err, db){
			if(err){
				throw err;
			}
			console.log("Database Created");
			var dbo= db.db("mydb");
			dbo.collection("Users").insertOne(data, function(err, res){
				if(err){
					throw err;
				}
				console.log("Một trường đã được thêm vào: "+ data);
			});
			db.close();
		});
		let name = data["username"];
		socket.un=name;
		result=true;
		socket.emit('ResultRegister',{content:result});	
	}
  });

  socket.on('ClientLogin', function(datalogin){
		socket.un= datalogin["Username"];
	for(i = 0; i<ListRegister.length; i++){
		if(datalogin["Username"] == ListRegister[i]["username"]&& datalogin["Password"]==ListRegister[i]["password"]){
			console.log("Login succeed!");
			result_login= true;
			if(ListLogin.length==0){
				ListLogin.push(datalogin);
			}else{
			for(k=0;k<ListLogin.length;k++){
				if(datalogin["Username"] == ListLogin[k]["Username"]){
					ListLogin[k]=datalogin;
					ListLogin.splice(k,1);
					break;
				}
			}
			ListLogin.push(datalogin);
			}
			
			ListSocketId.push(socket.id);
		console.log(ListLogin);
			break;
			
		}else{
			console.log("Login falled");
			result_login= false;
		}
	}
	
	console.log(result_login);
	socket.emit('ResultLogin', {content_login: result_login});
});

console.log(ListSocketId);
	
	socket.on('SendMessageToServer',function (dataChat){
	console.log(dataChat);
	find_friend= dataChat["name_friend"].toString();
	chat_message= dataChat["message"].toString();
	unique_id= dataChat["unique_id"].toString();
	id_socket= dataChat["id_socket"];
	
	for(i= 0;i< ListRegister.length;i++){
		if(find_friend== ListRegister[i]["username"]){
			id_find= ListRegister[i]["IDSocket"];
			break;
		}
	}
	for(j= 0;j< ListLogin.length;j++){
		if(find_friend== ListLogin[j]["Username"]){
			id_find_login= ListLogin[j]["id_sk"];
			break;
		}
		else{
			id_find_login=null;
		}
	}
	console.log(id_find_login);
	message_send={
		"name":socket.un,
		"message": chat_message,
		"unique_id": unique_id
	};
	
	io.to(`${id_find}`).emit('ServerSendMessageToAll',{contentChat:message_send});
	io.to(`${id_find_login}`).emit('ServerSendMessageToAll',{contentChat:message_send});
	io.to(`${id_socket}`).emit('ServerSendMessageToAll',{contentChat:message_send});
	 
	});
	
	socket.on('friends', function(friends){
		io.sockets.emit('server-send-username',{list_username: ListRegisterName});
	});
});
app.get("/", function(req, res){
	res.sendFile(__dirname + "/index.html");
	console.log("Hello World!");
});
