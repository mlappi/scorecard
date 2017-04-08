<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
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
	    	<li class="active"><a href="#">Course</a></li>
	    	<li><a href="/player"><span class="glyphicon glyphicon-user">  Players</span></a></li>
	        <li><a href="/game"><span class="glyphicon glyphicon-list-alt">  Games</span></a></li>	        	        	        
	    </ul>   
            <form action="/course/save" method="POST" data-toggle="validator" role="form">
                <input type="hidden" id="id" name="id" value="${course.id}">
                
                <h5>Name: </h5>
				<p>				
				<input type="text" name="name" id="name" class="form-control" value="${course.name}"/>
				</p>	                
                <div class="table-responsive">
                 <table class="table table-striped table-bordered">
                     <thead>
                         <tr>
                             <td class="text-center col-sm-1">1</td>
                             <td class="text-center col-sm-1">2</td>
                             <td class="text-center col-sm-1">3</td>
                             <td class="text-center col-sm-1">4</td>
                             <td class="text-center col-sm-1">5</td>
                             <td class="text-center col-sm-1">6</td>
                             <td class="text-center col-sm-1">7</td>
                             <td class="text-center col-sm-1">8</td>
                             <td class="text-center col-sm-1">9</td>
                             <td class="text-center col-sm-1">Out</td>
                         </tr>
                     </thead>
                     
                     	<c:forEach var="hole" items="${course.hole}" varStatus="loop">
                     		<input type="hidden" name="hole[${loop.index}].id" value="${hole.id}">
                     		<input type="hidden" name="hole[${loop.index}].hole" value="${hole.hole}">
                     		<c:if test ="${loop.index == 0}">
                     			<tr>
                     		</c:if>
                     		<c:if test ="${loop.index < 9}">                     		
                     			<td class="col-sm-1"><input type="text" onchange="countOut();" name="hole[${loop.index}].par" id="out_hole${loop.index+1}" class="form-control" value="${hole.par}"/></td>                     			
                     		</c:if>	
                     		<c:if test ="${loop.index > 8}">                     		
                     			<td class="col-sm-1"><input type="text" onchange="countIn();" name="hole[${loop.index}].par" id="in_hole${loop.index+1}" class="form-control" value="${hole.par}"/></td>
                     		</c:if>	                     		
                     		<c:if test ="${loop.index == 8}">
                     			<td class="col-sm-1"><input type="text" name="outSum" id="outSum" value="${course.countOut}" class="form-control" readonly="readonly"/></td>
                     		</c:if>
                     		<c:if test ="${loop.index == 8}">
                     		</tr>
		                     <thead>
		                         <tr>
		                             <td class="text-center col-sm-1">10</td>
		                             <td class="text-center col-sm-1">11</td>
		                             <td class="text-center col-sm-1">12</td>
		                             <td class="text-center col-sm-1">13</td>
		                             <td class="text-center col-sm-1">14</td>
		                             <td class="text-center col-sm-1">15</td>
		                             <td class="text-center col-sm-1">16</td>
		                             <td class="text-center col-sm-1">17</td>
		                             <td class="text-center col-sm-1">18</td>
		                             <td class="text-center col-sm-1">In</td>                                                          
		                         </tr>
		                     </thead>                     			
                     		<tr>
                     		</c:if>
						</c:forEach>
							<td class="col-sm-1"><input type=text name="inSum" id="inSum" value="${course.countIn}" class="form-control" readonly="readonly"/></td>	                         
                     		</tr>
                     <tr><td class="text-center">Total</td></tr>
                     <tr>                     
                     	  <td><input type="text" name="total" id="total" value="${course.countTotal}" class="form-control" readonly="readonly"/></td>	
                     </tr>
                  </table>                  
                </div>              
                <div>
                    <br>
                    <button type="submit" id="save" class="btn btn-primary btn-md">Save</button>
                    <button type="reset" class="btn btn-primary  btn-md">Reset</button>
                </div>    			                                                                                           
            </form>
	    <script type="text/javascript">
	 	    var h1 = document.getElementById("out_hole1"),
	 	    h2 = document.getElementById("out_hole2"),
	 	    h3 = document.getElementById("out_hole3"),
	 	   	h4 = document.getElementById("out_hole4"),
	 		h5 = document.getElementById("out_hole5"),
		 	h6 = document.getElementById("out_hole6"),
		 	h7 = document.getElementById("out_hole7"),
	 		h8 = document.getElementById("out_hole8"),
	 		h9 = document.getElementById("out_hole9"),
	 		h10 = document.getElementById("in_hole10"),
	 		h11 = document.getElementById("in_hole11"),
	 		h12 = document.getElementById("in_hole12"),
	 	    h13 = document.getElementById("in_hole13"),
	 	   	h14 = document.getElementById("in_hole14"),
	 		h15 = document.getElementById("in_hole15"),
		 	h16 = document.getElementById("in_hole16"),
		 	h17 = document.getElementById("in_hole17"),
	 		h18 = document.getElementById("in_hole18"),
	 	    save = document.getElementById("save");
	 	    
	 		h1.onkeyup = function(e) {
	 	    	var score = parseInt(h1.value, 10);	 	    	
	 	    	if (score > 1 && e.keyCode !=8 && e.keyCode !=9 && e.keyCode !=46) {	 	    		
	 	            h2.focus();
	 	        }
	 	    }
	 		h2.onkeyup = function(e) {
	 	    	var score = parseInt(h2.value, 10);
	 	    	if (score > 1 && e.keyCode !=8 && e.keyCode !=9 && e.keyCode !=46) {
	 	            h3.focus();
	 	        }
	 	    }
	 		h3.onkeyup = function(e) {
	 	    	var score = parseInt(h3.value, 10);
	 	    	if (score > 1 && e.keyCode !=8 && e.keyCode !=9 && e.keyCode !=46) {
	 	            h4.focus();
	 	        }
	 	    }
	 		h4.onkeyup = function(e) {
	 	    	var score = parseInt(h4.value, 10);
	 	    	if (score > 1 && e.keyCode !=8 && e.keyCode !=9 && e.keyCode !=46) {
	 	            h5.focus();
	 	        }
	 	    }
	 		h5.onkeyup = function(e) {
	 	    	var score = parseInt(h5.value, 10);
	 	    	if (score > 1 && e.keyCode !=8 && e.keyCode !=9 && e.keyCode !=46) {
	 	            h6.focus();
	 	        }
	 	    }
	 		h6.onkeyup = function(e) {
	 	    	var score = parseInt(h6.value, 10);
	 	    	if (score > 1 && e.keyCode !=8 && e.keyCode !=9 && e.keyCode !=46) {
	 	            h7.focus();
	 	        }
	 	    }
	 		h7.onkeyup = function(e) {
	 	    	var score = parseInt(h7.value, 10);
	 	    	if (score > 1 && e.keyCode !=8 && e.keyCode !=9 && e.keyCode !=46) {
	 	            h8.focus();
	 	        }
	 	    }
	 		h8.onkeyup = function(e) {
	 	    	var score = parseInt(h8.value, 10);
	 	    	if (score > 1 && e.keyCode !=8 && e.keyCode !=9 && e.keyCode !=46) {
	 	            h9.focus();
	 	        }
	 	    }
	 		h9.onkeyup = function(e) {
	 	    	var score = parseInt(h9.value, 10);
	 	    	if (score > 1 && e.keyCode !=8 && e.keyCode !=9 && e.keyCode !=46) {
	 	            h10.focus();
	 	        }
	 	    }
	 		h10.onkeyup = function(e) {
	 	    	var score = parseInt(h10.value, 10);
	 	    	if (score > 1 && e.keyCode !=8 && e.keyCode !=9 && e.keyCode !=46) {
	 	            h11.focus();
	 	        }
	 	    }
	 		h11.onkeyup = function(e) {
	 	    	var score = parseInt(h11.value, 10);
	 	    	if (score > 1 && e.keyCode !=8 && e.keyCode !=9 && e.keyCode !=46) {
	 	            h12.focus();
	 	        }
	 	    }
	 		h12.onkeyup = function(e) {
	 	    	var score = parseInt(h12.value, 10);
	 	    	if (score > 1 && e.keyCode !=8 && e.keyCode !=9 && e.keyCode !=46) {
	 	            h13.focus();
	 	        }
	 	    }
	 		h13.onkeyup = function(e) {
	 	    	var score = parseInt(h13.value, 10);
	 	    	if (score > 1 && e.keyCode !=8 && e.keyCode !=9 && e.keyCode !=46) {
	 	            h14.focus();
	 	        }
	 	    }
	 		h14.onkeyup = function(e) {
	 	    	var score = parseInt(h14.value, 10);
	 	    	if (score > 1 && e.keyCode !=8 && e.keyCode !=9 && e.keyCode !=46) {
	 	            h15.focus();
	 	        }
	 	    }
	 		h15.onkeyup = function(e) {
	 	    	var score = parseInt(h15.value, 10);
	 	    	if (score > 1 && e.keyCode !=8 && e.keyCode !=9 && e.keyCode !=46) {
	 	            h16.focus();
	 	        }
	 	    }
	 		h16.onkeyup = function(e) {
	 	    	var score = parseInt(h16.value, 10);
	 	    	if (score > 1 && e.keyCode !=8 && e.keyCode !=9 && e.keyCode !=46) {
	 	            h17.focus();
	 	        }
	 	    }
	 		h17.onkeyup = function(e) {
	 	    	var score = parseInt(h17.value, 10);
	 	    	if (score > 1 && e.keyCode !=8 && e.keyCode !=9 && e.keyCode !=46) {
	 	            h18.focus();
	 	        }
	 	    }
	 		h18.onkeyup = function(e) {
	 	    	var score = parseInt(h18.value, 10);
	 	    	if (score > 1 && e.keyCode !=8 && e.keyCode !=9 && e.keyCode !=46) {
	 	            save.focus();
	 	        }
	 	    }
	    
	    
   		</script>            
        </div>
 
	    <script type="text/javascript">
			function countOut(){
			    var arr = $('input[id^=out_]');
			    var tot=0;
			    for(var i=0;i<arr.length;i++){
			        if(parseInt(arr[i].value))
			            tot += parseInt(arr[i].value);
			    }
			    document.getElementById('outSum').value = tot;
			    var inSum = parseInt(document.getElementById('inSum').value) || 0;
			    document.getElementById('total').value = inSum + tot;
			    
			}
			function countIn(){
				var arr = $('input[id^=in_]');
			    var tot=0;
			    for(var i=0;i<arr.length;i++){
			        if(parseInt(arr[i].value))
			            tot += parseInt(arr[i].value);
			    }
			    document.getElementById('inSum').value = tot;
			    var outSum = parseInt(document.getElementById('outSum').value) || 0;
			    document.getElementById('total').value = outSum + tot;			    
			}			
   		</script>        
    </body>
</html>