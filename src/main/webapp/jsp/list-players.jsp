<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<html>
    <head>
        <link rel="stylesheet" href="../css/bootstrap.min.css">  
        <script src="http://code.jquery.com/jquery-3.1.1.min.js"></script> 		
        <script src="../js/bootstrap.min.js"></script>       
    </head>

    <body>          
        <div class="container">
	    <ul class="nav nav-tabs">
	    	<li class="active"><a href="#"><span class="glyphicon glyphicon-user">  Players</span></a></li>
	        <li><a href="/game">Games</a></li>	        
	    </ul> 
	    <br></br>
            <!--Search Form -->
            <form action="/player" method="get" id="seachPlayerForm" role="form">
                <input type="hidden" id="searchAction" name="searchAction" value="searchByName">
                <div class="form-group col-xs-5">
                    <input type="text" name="playerName" id="playerName" class="form-control" required="true" placeholder="Type the Name or Last Name of the player"/>                    
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
            <form action="/player" method="post" id="playerForm" role="form" >              
                <input type="hidden" id="idPlayer" name="idPlayer">
                <input type="hidden" id="action" name="action">
                <c:choose>
                    <c:when test="${not empty playerList}">
                        <table  class="table table-striped">
                            <thead>
                                <tr>
                                    <td>#</td>
                                    <td>Name</td>
                                    <td>Last name</td>
                                    <td>HCP</td>
                                    <td>E-mail</td>
                                    <td></td>
                                </tr>
                            </thead>
                            <c:forEach var="player" items="${playerList}">
                                <c:set var="classSucess" value=""/>
                                <c:if test ="${idPlayer == player.id}">                        	
                                    <c:set var="classSucess" value="info"/>
                                </c:if>
                                <tr class="${classSucess}">
                                    <td>
                                        <a href="/player?idPlayer=${player.id}&searchAction=searchById">${player.id}</a>
                                    </td>                                    
                                    <td>${player.name}</td>
                                    <td>${player.lastName}</td>
                                    <td>${player.hcp}</td>
                                    <td>${player.email}</td>   
                                    <td><a href="#" id="remove" 
                                           onclick="document.getElementById('action').value = 'remove';document.getElementById('idPlayer').value = '${player.id}';
                                                    
                                                    document.getElementById('playerForm').submit();"> 
                                            <span class="glyphicon glyphicon-trash"/>
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
            <form action ="jsp/new-player.jsp">            
                <br></br>
                <button type="submit" class="btn btn-primary  btn-md">New player</button> 
            </form>
        </div>
    </body>
</html>