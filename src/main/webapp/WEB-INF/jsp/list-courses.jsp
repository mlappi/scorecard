<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<html>
    <head>
    	<title>Golf Score App</title>
        <link rel="stylesheet" href="../css/bootstrap.min.css">   		
        <script src="http://code.jquery.com/jquery-3.1.1.min.js"></script>
        <script src="../js/bootstrap.min.js"></script>               
    </head>

    <body>          
        <div class="container">
	    <ul class="nav nav-tabs">
	    	<li><a href="/" class="glyphicon glyphicon-home">  Home</a></li>
	    	<li class="active"><a href="#"><span class="glyphicon glyphicon-flag">  Courses</span></a></li>
	    	<li><a href="/player"><span class="glyphicon glyphicon-user">  Players</span></a></li>
	        <li><a href="/game"><span class="glyphicon glyphicon-list-alt">  Games</span></a></li>	        
	    </ul>          
        <br></br>            
            <c:if test="${not empty message}">                
                <div class="alert alert-success">
                    ${message}
                </div>
            </c:if> 
            <form action="/course" method="post" id="courseForm" role="form" >              
                <input type="hidden" id="idCourse" name="idCourse">
                <c:choose>
                    <c:when test="${not empty courses}">
                        <table  class="table table-striped">
                            <thead>
                                <tr>
                                    <td>#</td>
                                    <td>Name</td>
                                    <td></td>
                                    <td></td>
                                </tr>
                            </thead>
                            <c:forEach var="course" items="${courses}" varStatus="loop">
                                <c:set var="classSucess" value=""/>
                                <c:if test ="${idCourse == course.id}">                        	
                                    <c:set var="classSucess" value="info"/>
                                </c:if>
                                <tr class="${classSucess}">
                                    <td>${loop.index +1}</td>                                    
                                    <td><a href="/course/edit/${course.id}">${course.name}</a></td>
                                    <td><a href="/course/remove/${course.id}" id="remove"> 
                                            <span class="glyphicon glyphicon-trash"></span>
                                        </a>                                                   
                                    </td>
                                    <td><a href="/course/edit/${course.id}" id="edit"> 
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
                            No courses added
                        </div>
                    </c:otherwise>
                </c:choose>                        
            </form>
            <form action ="/course/new">            
                <br></br>
                <button type="submit" class="btn btn-primary  btn-md">New Course</button> 
            </form>
        </div>
    </body>
</html>