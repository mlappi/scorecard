<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<html>
    <head>
    	<title>Golf Score App</title>
        <link rel="stylesheet" href="../../css/bootstrap.min.css">   		
        <script src="http://code.jquery.com/jquery-3.1.1.min.js"></script>
        <script src="../../js/bootstrap.min.js"></script>             
    </head>
    <body>
        <div class="container">
	    <ul class="nav nav-tabs">
	    	<li><a href="/" class="glyphicon glyphicon-home">  Home</a></li>
	    	<li><a href="/course"><span class="glyphicon glyphicon-flag">  Courses</span></a></li>
	    	<li><a href="/player"><span class="glyphicon glyphicon-user">  Players</span></a></li>
	        <li><a href="/game"><span class="glyphicon glyphicon-list-alt">  Games</span></a></li>	        
	        <li class="active"><a href="#">Player</a></li>
	    </ul>  
        
            <form action="/player/save" method="post" role="form" data-toggle="validator" >
                <input type="hidden" id="id" name="id" value="${player.id}">
                <h4>Player</h4>
                <div class="form-group col-xs-4">
                    <label for="name" class="control-label col-xs-4">First name:</label>
                    <input type="text" name="firstName" id="firstName" class="form-control" value="${player.firstName}" required="true"/>                                   

                    <label for="lastName" class="control-label col-xs-4">Last name:</label>                   
                    <input type="text" name="lastName" id="lastName" class="form-control" value="${player.lastName}" required="true"/> 

                    <label for="hcp" class="control-label col-xs-4">Hcp:</label>
                    <input type="number" step=".1" name="hcp" id="hcp" class="form-control" value="${player.hcp}" />

                    <label for="email" class="control-label col-xs-4">Email:</label>                   
                    <input type="text" name="email" id="email" class="form-control" value="${player.email}" placeholder="first.last@gmail.com"/>

                    <br></br>
                    <button type="submit" class="btn btn-primary  btn-md">Save</button>
                    <a href="/player" class="btn btn-primary btn-md">Back</a>  
                </div>                                                      
            </form>
        </div>
    </body>
</html>