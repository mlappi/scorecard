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
	    	<li><a href="/"><span class="glyphicon glyphicon-user">  Players</span></a></li>
	        <li class="active"><a href="#">Games</a></li>	        
	    </ul>          
            <h2>Games</h2>
            <!--Search Form -->
            <form action="/game" method="get" id="seachGameForm" role="form">
                <input type="hidden" id="searchAction" name="searchAction" value="searchByName">
                <div class="form-group col-xs-5">
                    <input type="text" name="gameName" id="gameName" class="form-control" required="true" placeholder="Type the Name of the game"/>                    
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
            <form action="/game" method="post" id="gameForm" role="form" >              
                <input type="hidden" id="idGame" name="idGame">
                <input type="hidden" id="action" name="action">
                <c:choose>
                    <c:when test="${not empty gameList}">
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
                            <c:forEach var="game" items="${gameList}">
                                <c:set var="classSucess" value=""/>
                                <c:if test ="${idGame == game.id}">                        	
                                    <c:set var="classSucess" value="info"/>
                                </c:if>
                                <tr class="${classSucess}">
                                    <td>
                                        <a href="/game?idGame=${game.id}&searchAction=searchById">${game.id}</a>
                                    </td>                                    
                                    <td>${game.name}</td>
                                    <td>${game.date}</td>
                                    <td>${game.bet}</td>   
                                    <td><a href="#" id="remove" 
                                           onclick="document.getElementById('action').value = 'remove';document.getElementById('idGame').value = '${game.id}';                                                    
                                                    document.getElementById('gameForm').submit();"> 
                                            <span class="glyphicon glyphicon-trash"/>
                                        </a>                                                   
                                    </td>
                                    <td><a href="#" id="select" 
                                           onclick="document.getElementById('action').value = 'select';document.getElementById('idGame').value = '${game.id}';                                                    
                                                    document.getElementById('gameForm').submit();"> 
                                            <span class="glyphicon glyphicon-edit"/>
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
            <form action ="jsp/new-game.jsp">            
                <br></br>
                <button type="submit" class="btn btn-primary  btn-md">New Game</button> 
            </form>
        </div>
    </body>
</html>