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
	    	<li class="active"><a href="#"><span class="glyphicon glyphicon-user">  Players</span></a></li>
	        <li><a href="/game"><span class="glyphicon glyphicon-list-alt">  Games</span></a></li>	        
	    </ul> 
	    <br></br>
            <!--Search Form -->
            <form action="/player/search" method="post" id="seachPlayerForm" role="form">
                <div class="form-group col-xs-5">
                    <input type="text" name="playerName" id="playerName" class="form-control" 
                    	placeholder="Type first name or last name"/>                    
                </div>
                <button type="submit" class="btn btn-info">
                    <span class="glyphicon glyphicon-search"></span> Search
                </button>                
                <br></br>
            </form>

            <!--Players List-->
            <c:if test="${not empty message}">                
                <div class="alert alert-success">
                    ${message}
                </div>
            </c:if> 
            <c:if test="${not empty errormessage}">                
                <div class="alert alert-warning">
                    ${errormessage}
                </div>
            </c:if> 
            
            <form action="/player" method="post" id="playerForm" role="form" >              
                <input type="hidden" id="idPlayer" name="idPlayer">
                <c:choose>
                    <c:when test="${not empty players}">
                        <table  class="table table-striped">
                            <thead>
                                <tr>
                                    <td>#</td>
                                    <td>Name</td>
                                    <td>HCP</td>
                                    <td>E-mail</td>
                                    <td></td>
                                </tr>
                            </thead>
                            <c:forEach var="player" items="${players}" varStatus="loop">
                                <c:set var="classSucess" value=""/>
                                <c:if test ="${idPlayer == player.id}">                        	
                                    <c:set var="classSucess" value="info"/>
                                </c:if>
                                <tr class="${classSucess}">
                                    <td>${loop.index +1}</td>                                    
                                    <td><a href="/player/edit/${player.id}">${player.firstName} ${player.lastName}</a></td>
                                    <td>${player.hcp}</td>
                                    <td>${player.email}</td>   
                                    <td><a href="/player/remove/${player.id}" id="remove"> 
                                            <span class="glyphicon glyphicon-trash"></span>
                                        </a>                                                   
                                    </td>
                                </tr>
                            </c:forEach>               
                        </table>  
                    </c:when>                    
                    <c:otherwise>
                        <br>           
                        <div class="alert alert-info">
                            No people found matching your search criteria
                        </div>
                    </c:otherwise>
                </c:choose>                        
            </form>
            <form action ="/player/new">            
                <br></br>
                <button type="submit" class="btn btn-primary  btn-md">New player</button> 
            </form>
        </div>
    </body>
</html>