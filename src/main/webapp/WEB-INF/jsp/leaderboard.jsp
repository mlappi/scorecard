<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<html>
    <head>
    	<title>Golf Score App</title>
        <link rel="stylesheet" href="../../css/bootstrap.min.css">  
        <script src="http://code.jquery.com/jquery-3.1.1.min.js"></script> 		
        <script src="../../js/bootstrap.min.js"></script>       
    </head>

    <body>          
        <div class="container">	    <ul class="nav nav-tabs">
	    	<li><a href="/" class="glyphicon glyphicon-home">  Home</a></li>
	    	<li><a href="/course"><span class="glyphicon glyphicon-flag">  Courses</span></a></li>
	    	<li><a href="/player"><span class="glyphicon glyphicon-user">  Players</span></a></li>
	        <li><a href="/game"><span class="glyphicon glyphicon-list-alt">  Games</span></a></li>	        
	        <li class="active"><a href="#"><span class="glyphicon glyphicon-list-alt">  Leaderboard</span></a></li>
	    </ul> 
		<br></br>

            <!--Players List-->
            <c:if test="${not empty message}">                
                <div class="alert alert-success">
                    ${message}
                </div>
            </c:if> 
            <c:choose>
                <c:when test="${not empty scores}">
                    <table  class="table table-striped">
                        <thead>
                            <tr>
                                <td>#</td>
                                <td>Name</td>
                                <c:forEach var="hole" items="${rounds}" varStatus="loop">
                                	<td>R${loop.index +1}</td>
                                </c:forEach>	
                                <td>Total</td>
                                <td>+/-</td>
                            </tr>
                        </thead>
                        <c:forEach var="score" items="${scores}" varStatus="loop">
                            <tr class="${classSucess}">
                                <td>${loop.index +1}</td>                                    
                                <td>${score.name}</td>
                                <c:forEach var="roundScore" items="${scores[loop.index].score}" varStatus="loop">
                                	<td>${roundScore}</td>
                                </c:forEach>	                                
                                <td>${score.totalAll}</td>
                                <td>${score.total}</td>   
                            </tr>
                        </c:forEach>               
                    </table>  
                </c:when>                    
                <c:otherwise>
                    <br>           
                    <div class="alert alert-info">
                        No scores
                    </div>
                </c:otherwise>
              </c:choose>                        
            <form action ="/game">            
                <br></br>
                <button type="submit" class="btn btn-primary  btn-md">Back</button> 
            </form>
        </div>
    </body>
</html>
