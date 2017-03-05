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
	        <li><a href="/game">Games</a></li>	        
	        <li><a href="/game">Game</a></li>
	        <li class="active"><a href="#">Scorecard</a></li>
	    </ul>   
            <form action="/score" method="post" role="form" data-toggle="validator" >
                <c:if test ="${empty action}">                        	
                    <c:set var="action" value="add"/>
                </c:if>
                <input type="hidden" id="action" name="action" value="${action}">
                <input type="hidden" id="idScorecard" name="idScorecard" value="${player.id}">
                <div class="table-responsive">
                 <table class="table table-striped table-bordered">
	                <caption>
	                    ${player.name} ${player.lastName} 
	                </caption>
                     <thead>
                         <tr>
                             <td class="text-center">1</td>
                             <td class="text-center">2</td>
                             <td class="text-center">3</td>
                             <td class="text-center">4</td>
                             <td class="text-center">5</td>
                             <td class="text-center">6</td>
                             <td class="text-center">7</td>
                             <td class="text-center">8</td>
                             <td class="text-center">9</td>
                             <td class="text-center">Out</td>
                         </tr>
                     </thead>
                     <tr>
                          <td><input type="text" onchange="countOut();" name="out" id="hole1" class="form-control" placeholder="5" value="${score.hole1}"/></td>  
                          <td><input type="text" onchange="countOut();" name="out" id="hole2" class="form-control" placeholder="4" value="${score.hole2}"/></td>
                          <td><input type="text" onchange="countOut();" name="out" id="hole3" class="form-control" placeholder="3" value="${score.hole3}"/></td>
                          <td><input type="text" onchange="countOut();" name="out" id="hole4" class="form-control" placeholder="4" value="${score.hole4}"/></td>                          
                          <td><input type="text" onchange="countOut();" name="out" id="hole5" class="form-control" placeholder="3" value="${score.hole5}"/></td>
                          <td><input type="text" onchange="countOut();" name="out" id="hole6" class="form-control" placeholder="5" value="${score.hole6}"/></td>
                          <td><input type="text" onchange="countOut();" name="out" id="hole7" class="form-control" placeholder="4" value="${score.hole7}"/></td>
                          <td><input type="text" onchange="countOut();" name="out" id="hole8" class="form-control" placeholder="3" value="${score.hole8}"/></td>
                          <td><input type="text" onchange="countOut();" name="out" id="hole9" class="form-control" placeholder="4" value="${score.hole9}"/></td>
                          <td><input type="text" name="outSum" id="outSum" class="form-control" readonly="readonly"/></td>
                     </tr>
                  </table>
                  <table class="table table-striped table-bordered">
                     <thead>
                         <tr>
                             <td class="text-center">10</td>
                             <td class="text-center">11</td>
                             <td class="text-center">12</td>
                             <td class="text-center">13</td>
                             <td class="text-center">14</td>
                             <td class="text-center">15</td>
                             <td class="text-center">16</td>
                             <td class="text-center">17</td>
                             <td class="text-center">18</td>
                             <td class="text-center">In</td>                             
                         </tr>                         
                     </thead>
                     <tr>
                          <td><input type="text" onchange="countIn();" name="in" name="hole10" id="hole10" class="form-control" placeholder="4" value="${score.hole10}"/></td>
                          <td><input type="text" onchange="countIn();" name="in" name="hole11" id="hole11" class="form-control" placeholder="5" value="${score.hole11}"/></td>  
                          <td><input type="text" onchange="countIn();" name="in" name="hole12" id="hole12" class="form-control" placeholder="4" value="${score.hole12}"/></td>
                          <td><input type="text" onchange="countIn();" name="in" name="hole13" id="hole13" class="form-control" placeholder="3" value="${score.hole13}"/></td>
                          <td><input type="text" onchange="countIn();" name="in" name="hole14" id="hole14" class="form-control" placeholder="4" value="${score.hole14}"/></td>                          
                          <td><input type="text" onchange="countIn();" name="in" name="hole15" id="hole15" class="form-control" placeholder="3" value="${score.hole15}"/></td>
                          <td><input type="text" onchange="countIn();" name="in" name="hole16" id="hole16" class="form-control" placeholder="5" value="${score.hole16}"/></td>
                          <td><input type="text" onchange="countIn();" name="in" name="hole17" id="hole17" class="form-control" placeholder="4" value="${score.hole17}"/></td>
                          <td><input type="text" onchange="countIn();" name="in" name="hole18" id="hole18" class="form-control" placeholder="4" value="${score.hole18}"/></td>                          
                          <td><input type="text" name="inSum" id="inSum" class="form-control" readonly="readonly"/></td>
                          
                     </tr>
                     <tr><td class="text-center">Total</td></tr>
                     <tr>                     
                     	  <td><input type="text" name="total" id="total" class="form-control" readonly="readonly"/></td>	
                     </tr>
                  </table>                  
                </div>              
                <div>
                    <br>
                    <button type="submit" id="save" class="btn btn-primary btn-md">Save</button>
                    <button type="reset" class="btn btn-primary  btn-md">Clear</button>
                </div>    			                                                                                           
            </form>
	    <script type="text/javascript">
	 	    var h1 = document.getElementById("hole1"),
	 	    h2 = document.getElementById("hole2"),
	 	    h3 = document.getElementById("hole3"),
	 	   	h4 = document.getElementById("hole4"),
	 		h5 = document.getElementById("hole5"),
		 	h6 = document.getElementById("hole6"),
		 	h7 = document.getElementById("hole7"),
	 		h8 = document.getElementById("hole8"),
	 		h9 = document.getElementById("hole9"),
	 		h10 = document.getElementById("hole10"),
	 		h11 = document.getElementById("hole11"),
	 		h12 = document.getElementById("hole12"),
	 	    h13 = document.getElementById("hole13"),
	 	   	h14 = document.getElementById("hole14"),
	 		h15 = document.getElementById("hole15"),
		 	h16 = document.getElementById("hole16"),
		 	h17 = document.getElementById("hole17"),
	 		h18 = document.getElementById("hole18"),
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
			    var arr = document.getElementsByName('out');
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
			    var arr = document.getElementsByName('in');
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