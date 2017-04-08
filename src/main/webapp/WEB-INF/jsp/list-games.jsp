<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<html>
    <head>
    	<title>Golf Score App</title>
        <link rel="stylesheet" href="../../css/bootstrap.min.css">   		
        <script src="../../js/jquery-3.1.1.min.js"></script>
        <script src="../../js/bootstrap.min.js"></script>               
    </head>

    <body>          
        <div class="container">
	    <ul class="nav nav-tabs">
	    	<li><a href="/" class="glyphicon glyphicon-home">  Home</a></li>
	    	<li><a href="/course"><span class="glyphicon glyphicon-flag">  Courses</span></a></li>
	    	<li><a href="/player"><span class="glyphicon glyphicon-user">  Players</span></a></li>
	        <li class="active"><a href="#"><span class="glyphicon glyphicon-list-alt">  Games</span></a></li>	        
	    </ul>          
        <br></br>            
            <!--Search Form -->            
            <form action="/game/search" method="post" id="seachGameForm" role="form">
                <div class="form-group col-xs-5">                	                
                    <input type="text" name="name" id="name" class="form-control" placeholder="Type the name of the game"/>                    
                </div>
                <button type="submit" class="btn btn-info">
                    <span class="glyphicon glyphicon-search"></span> Search
                </button>
                <br></br>
                <br></br>
            </form>

            <!--Games List-->
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
            <form action="/game" method="post" id="gameForm" role="form" >              
                <input type="hidden" id="idGame" name="idGame">
                <c:choose>
                    <c:when test="${not empty games}">
                        <table  class="table table-striped">
                            <thead>
                                <tr>
                                    <td>#</td>
                                    <td>Name</td>
                                    <td>Date</td>
                                    <td>Bet</td>
                                    <td></td>
                                    <td></td>
                                </tr>
                            </thead>
                            <c:forEach var="game" items="${games}" varStatus="loop">
                                <c:set var="classSucess" value=""/>
                                <c:if test ="${idGame == game.id}">                        	
                                    <c:set var="classSucess" value="info"/>
                                </c:if>
                                <tr class="${classSucess}">
                                    <td>${loop.index +1}</td>                                    
                                    <td><a href="/score/list/${game.id}">${game.name}</a></td>
                                    <td><fmt:formatDate value="${game.date}" pattern="dd.MM.yyyy HH:mm"></fmt:formatDate></td>
                                    <td>${game.bet}</td>   
                                    <td><a href="/game/remove/${game.id}" id="remove"> 
                                            <span class="glyphicon glyphicon-trash"></span>
                                        </a>                                                   
                                    </td>
                                    <td><a href="/game/edit/${game.id}" id="edit"> 
                                            <span class="glyphicon glyphicon-edit"></span>
                                        </a>                                                   
                                    </td>                                    
                                </tr>
                            </c:forEach>               
                        </table>  
                    </c:when>                    
                    <c:otherwise>
                        <br>           
                        <div class="alert alert-info">
                            No games found matching your search criteria
                        </div>
                    </c:otherwise>
                </c:choose>                        
            </form>
            <form action ="/game/new">            
                <br></br>
                <button type="submit" class="btn btn-primary  btn-md">New Game</button> 
            </form>
        </div>
    </body>
</html>