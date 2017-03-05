<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<html>
    <head>
        <link rel="stylesheet" href="../../css/bootstrap.min.css">   		
        <script src="http://code.jquery.com/jquery-3.1.1.min.js"></script>
        <script src="../../js/bootstrap.min.js"></script>             
    </head>
    <body>
        <div class="container">
            <form action="/player/save" method="post" role="form" data-toggle="validator" >
                <input type="hidden" id="id" name="id" value="${player.id}">
                <h2>Player</h2>
                <div class="form-group col-xs-4">
                    <label for="name" class="control-label col-xs-4">First name:</label>
                    <input type="text" name="firstName" id="firstName" class="form-control" value="${player.firstName}" required="true"/>                                   

                    <label for="lastName" class="control-label col-xs-4">Last name:</label>                   
                    <input type="text" name="lastName" id="lastName" class="form-control" value="${player.lastName}" required="true"/> 

                    <label for="hcp" class="control-label col-xs-4">Hcp:</label>
                    <input type="text" name="hcp" id="hcp" class="form-control" value="${player.hcp}" />

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