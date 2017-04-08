<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<html>
    <head>
    	<title>Golf Score App</title>
        <link rel="stylesheet" href="../../../css/bootstrap.min.css">   		
        <link rel="stylesheet" href="../../../css/bootstrap-datetimepicker.min.css">
        <script src="http://code.jquery.com/jquery-3.1.1.min.js"></script>
        <script src="../../../js/bootstrap.min.js"></script>                
        <script src="../../../js/bootstrap-datetimepicker.min.js"></script>
    </head>
    <body>
        <div class="container">
	    <ul class="nav nav-tabs">
	    	<li><a href="/" class="glyphicon glyphicon-home">  Home</a></li>
	    	<li><a href="/course"><span class="glyphicon glyphicon-flag">  Courses</span></a></li>
	    	<li><a href="/player"><span class="glyphicon glyphicon-user">  Players</span></a></li>
	        <li><a href="/game"><span class="glyphicon glyphicon-list-alt">  Games</span></a></li>	        
	        <li class="active"><a href="#"><span class="glyphicon glyphicon-asterisk">  Game</span></a></li>
	    </ul>  
        
            <form action="/game/save" method="post" role="form" data-toggle="validator" >                
                <input type="hidden" name="id" value="${game.id}">                
                <h4>Game</h4>

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
                
                <div class="form-group">                                  	
                  	<div class="col-md-4">
                  	<label for="gamename" class="control-label">Name:</label>
                    	<input type="text" name="name" id="gamename" class="form-control" value="${game.name}" required="true"/>
                    </div>
	                <label for="date" class="control-label">Date:</label>
	                <div class="input-group date col-md-2" id="datetimepicker1">	      	                          	
		                 <input type='text' class="form-control col-md-5" name="date" 
		                 value = "<fmt:formatDate value="${game.date}" pattern="dd.MM.yyyy HH:mm" />"
		                 readonly/>		                 
		                 <span class="input-group-addon">
		                     <span class="glyphicon glyphicon-calendar"></span>
		                 </span>
	           		</div>

                </div>                                                                        					
                 <div>   
                    <div class="form-group">
                    <h4>Rounds</h4>                    
                    <table  class="table table-striped">
                        <thead>
                            <tr>                               
                                <td class="col-md-1">#</td>
                                <td class="col-md-2">Course</td>
                                <td class="col-md-3">Round name</td>
                                <td class="col-md-3">Date</td>
                                <td class="col-md-1">Bet</td>                                
                                <td class="col-md-1"></td>
                            </tr>
                        </thead>
                        <c:forEach var="round" items="${game.round}" varStatus="loop2">
                            <c:set var="classSucess" value=""/>
                            <c:if test ="${idGame == round.id}">                        	
                                <c:set var="classSucess" value="info"/>
                            </c:if>                            
                         <tr class="${classSucess}">
                         	<input type="hidden" name="round[${loop2.index}].id" value="${round.id}">                         	                         
                            <td>${loop2.index +1}</td>                             
                            <td>
								<form:select path="game.round[${loop2.index}].course.id" >
								    <form:options items="${courseList}"/>
								</form:select>                            
                            </td>                                                               
                            <td><input type="text" name="round[${loop2.index}].name" class="form-control" value="${round.name}" required="true"/></td>
                            <td class="input-group date datetimepicker" id="datetimepicker">
		                 		<input type='text' class="form-control" name="round[${loop2.index}].date" 
		                 			value = "<fmt:formatDate value="${round.date}" pattern="dd.MM.yyyy HH:mm" />" readonly/>
			                 	<span class="input-group-addon">
			                    	<span class="glyphicon glyphicon-calendar"></span>
		                 		</span>						                                                     
                                </td>
                                <td><input type="number" name="round[${loop2.index}].bet" step=".1" class="form-control bfh-number" value="${round.bet}" required="true" /></td>                                   
                                <td>
			                     <c:if test ="${round.id == null}">
			                     	<a href="/game/edit/${game.id}">
			                     		<span class="glyphicon glyphicon-trash"></span>
			                     	</a>
			                     </c:if>
			                     <c:if test ="${round.id != null}">                                
                                	<a href="/game/remove-round/${game.id}/${round.id}" id="remove"> 
                                        <span class="glyphicon glyphicon-trash"></span>
                                    </a>                                               
                                  </c:if>     
                                </td>
                          </tr>
                        </c:forEach>                                        
                     </table>
                     <c:if test ="${game.id != null}">
                     	<a href="/game/addRound/${game.id}" class="btn btn-primary btn-md">New Round</a>
                     </c:if>
                    </div>      
					</br>                    
                    
                    <button type="submit" class="btn btn-primary btn-md">Save</button> 
                    <a href="/game" class="btn btn-info btn-md">Back</a>
        <script type="text/javascript">
        
        (function($){        	
        	$.fn.datetimepicker.dates['en'] = {
        		days: ["sunnuntai", "maanantai", "tiistai", "keskiviikko", "torstai", "perjantai", "lauantai", "sunnuntai"],
        		daysShort: ["sun", "maa", "tii", "kes", "tor", "per", "lau", "sun"],
        		daysMin: ["su", "ma", "ti", "ke", "to", "pe", "la", "su"],
        		months: ["tammikuu", "helmikuu", "maaliskuu", "huhtikuu", "toukokuu", "kes‰kuu", "hein‰kuu", "elokuu", "syyskuu", "lokakuu", "marraskuu", "joulukuu"],
        		monthsShort: ["tam", "hel", "maa", "huh", "tou", "kes", "hei", "elo", "syy", "lok", "mar", "jou"],
        		today: "t‰n‰‰n",
        		suffix: [],
        		meridiem: []
        	};
        }(jQuery));
        
        $('.datetimepicker').each(function(){
            $(this).datetimepicker({
                	locale: 'en',	
                	format: 'dd.mm.yyyy HH:ii',
                	autoclose: true,
                	pickerPosition: "bottom-left",
                	minuteStep: 10,
                	todayBtn: true 
            })
        });
        
        $('#datetimepicker1').datetimepicker({
        	locale: 'en',	
        	format: 'dd.mm.yyyy HH:ii',
        	autoclose: true,
        	pickerPosition: "bottom-left",
        	minuteStep: 10,
        	todayBtn: true
        });
        
        </script>                    
        </div>
        </form>
        </div>
    </body>
</html>

<!--                     <label for="bet" class="control-label">Bet:</label> -->
<%--                     <input type="number" name="bet" id="bet" step=".1" class="form-control bfh-number" value="${game.bet}" required="true" /> --%>
