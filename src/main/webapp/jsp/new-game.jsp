<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<html>
    <head>
        <link rel="stylesheet" href="../css/bootstrap.min.css">   		
        <script src="http://code.jquery.com/jquery-3.1.1.min.js"></script>
        <script src="../js/bootstrap.min.js"></script>     
    </head>
    <body>
        <div class="container">
            <form action="/game" method="post"  role="form" data-toggle="validator" >
                <c:if test ="${empty action}">                        	
                    <c:set var="action" value="add"/>
                </c:if>
                <input type="hidden" id="action" name="action" value="${action}">
                <input type="hidden" id="idGame" name="idGame" value="${game.id}">
                <h2>Game</h2>
                <div class="form-group col-xs-4">
                    <label for="name" class="control-label col-xs-4">Name:</label>
                    <input type="text" name="name" id="name" class="form-control" value="${game.name}" required="true"/>                                   

                    <label for="date" class="control-label col-xs-4">Date</label>                 
                    <!-- TODO: date picker -->
                    <input type="text" pattern="^\d{2}.\d{2}.\d{4}$" name="date" id="date" class="form-control" value="${game.date}" maxlength="10" placeholder="01.01.2017" required="true"/>

                    <label for="bet" class="control-label col-xs-4">Bet:</label>
                    <input type="text" name="bet" id="hcp" class="form-control bfh-number" value="${game.bet}" required="true" />
					
                    <br></br>
                    <button type="submit" class="btn btn-primary btn-md">Save</button> 
                </div>
            </form>
        </div>
    </body>
</html>