<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<html>
    <head>
        <link rel="stylesheet" href="../css/bootstrap.min.css">   		
        <script src="http://code.jquery.com/jquery-3.1.1.min.js"></script>
        <script src="../js/bootstrap.min.js"></script>             
    </head>
    <body>
        <div class="container">
            <form action="/player" method="post" role="form" data-toggle="validator" >
                <c:if test ="${empty action}">                        	
                    <c:set var="action" value="add"/>
                </c:if>
                <input type="hidden" id="action" name="action" value="${action}">
                <input type="hidden" id="idPlayer" name="idPlayer" value="${player.id}">
                <h2>Player</h2>
                <div class="form-group col-xs-4">
                    <label for="name" class="control-label col-xs-4">Name:</label>
                    <input type="text" name="name" id="name" class="form-control" value="${player.name}" required="true"/>                                   

                    <label for="lastName" class="control-label col-xs-4">Last name:</label>                   
                    <input type="text" name="lastName" id="lastName" class="form-control" value="${player.lastName}" required="true"/> 

                    <label for="hcp" class="control-label col-xs-4">HCP:</label>
                    <input type="text" name="hcp" id="hcp" class="form-control" value="${player.hcp}" />

                    <label for="email" class="control-label col-xs-4">E-mail:</label>                   
                    <input type="text" name="email" id="email" class="form-control" value="${player.email}" placeholder="first.last@gmail.com"/>

                    <br></br>
                    <button type="submit" class="btn btn-primary  btn-md">Save</button> 
                </div>                                                      
            </form>
        </div>
    </body>
</html>