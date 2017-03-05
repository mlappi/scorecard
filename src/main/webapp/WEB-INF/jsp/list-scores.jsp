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
	        <li class="active"><a href="#">Game</a></li>
	    </ul>  
	    <br/>        
            <!-- scores-->
            <c:if test="${not empty message}">                
                <div class="alert alert-success">
                    ${message}
                </div>
            </c:if> 
            <form action="/game" method="post" id="scoresForm" role="form" >              
                <input type="hidden" id="idScore" name="idScore">
                <input type="hidden" id="action" name="action">
                <c:choose>
                    <c:when test="${not empty scoreList}">
                        <table  class="table table-striped">
	                <caption>
	                    <span>${game.name}  |  </span><span>${game.date}  |  </span><span>${game.bet} EUR</span>
	                </caption>
                            <thead>
                                <tr>
                                    <td>#</td>
                                    <td><span class="nowrap">Player</span></td>
                                    <td>1</td>
                                    <td>2</td>
                                    <td>3</td>
                                    <td>4</td>
                                    <td>5</td>
                                    <td>6</td>
                                    <td>7</td>
                                    <td>8</td>
                                    <td>9</td>
                                    <td>Out</td>
                                    <td>10</td>
                                    <td>11</td>
                                    <td>12</td>
                                    <td>13</td>
                                    <td>14</td>
                                    <td>15</td>
                                    <td>16</td>
                                    <td>17</td>
                                    <td>18</td>
                                    <td>In</td>
                                    <td>Total</td>
                                    <td class="glyphicon glyphicon-eur"></td>
                                    <td></td>
                                    <td></td>
                                </tr>  
                                <tr>
                                    <td></td>
                                    <td></td>
                                    <td><small>${game.getPar(1)}</small></td>
                                    <td><small>${game.getPar(2)}</small></td>
                                    <td><small>${game.getPar(3)}</small></td>
                                    <td><small>${game.getPar(4)}</small></td>
                                    <td><small>${game.getPar(5)}</small></td>
                                    <td><small>${game.getPar(6)}</small></td>
                                    <td><small>${game.getPar(7)}</small></td>
                                    <td><small>${game.getPar(8)}</small></td>
                                    <td><small>${game.getPar(9)}</small></td>
                                    <td></td>
                                    <td><small>${game.getPar(10)}</small></td>
                                    <td><small>${game.getPar(11)}</small></td>
                                    <td><small>${game.getPar(12)}</small></td>
                                    <td><small>${game.getPar(13)}</small></td>
                                    <td><small>${game.getPar(14)}</small></td>
                                    <td><small>${game.getPar(15)}</small></td>
                                    <td><small>${game.getPar(16)}</small></td>
                                    <td><small>${game.getPar(17)}</small></td>
                                    <td><small>${game.getPar(18)}</small></td>
                                    <td></td>
                                    <td></td>
                                    <td></td>
                                    <td></td>
                                    <td></td>
                                </tr>                                                              
                            </thead>
                            <c:forEach var="score" items="${scoreList}" varStatus="loop">
                                <c:set var="classSucess" value=""/>
                                <c:if test ="${idScore == score.id}">                        	
                                    <c:set var="classSucess" value="info"/>
                                </c:if>
                                <tr class="${classSucess}">
                                    <td>
                                        <a href="/score?idScore=${score.id}&searchAction=searchById">${loop.index +1}</a>
                                    </td>                                    
                                    <td><span class="nowrap">${score.player}</span></td>
                                    <td class="${score.isWinner(1) ? 'bg-danger text-danger' : ''}">${score.hole1}</td>
                                    <td class="${score.isWinner(2) ? 'bg-danger text-danger' : ''}">${score.hole2}</td>
                                    <td class="${score.isWinner(3) ? 'bg-danger text-danger' : ''}">${score.hole3}</td>
                                    <td class="${score.isWinner(4) ? 'bg-danger text-danger' : ''}">${score.hole4}</td>
                                    <td class="${score.isWinner(5) ? 'bg-danger text-danger' : ''}">${score.hole5}</td>
                                    <td class="${score.isWinner(6) ? 'bg-danger text-danger' : ''}">${score.hole6}</td>
                                    <td class="${score.isWinner(7) ? 'bg-danger text-danger' : ''}">${score.hole7}</td>
                                    <td class="${score.isWinner(8) ? 'bg-danger text-danger' : ''}">${score.hole8}</td>
                                    <td class="${score.isWinner(9) ? 'bg-danger text-danger' : ''}">${score.hole9}</td>
                                    <td>${score.in}</td>
                                    <td class="${score.isWinner(10) ? 'bg-danger text-danger' : ''}">${score.hole10}</td>
                                    <td class="${score.isWinner(11) ? 'bg-danger text-danger' : ''}">${score.hole11}</td>
                                    <td class="${score.isWinner(12) ? 'bg-danger text-danger' : ''}">${score.hole12}</td>
                                    <td class="${score.isWinner(13) ? 'bg-danger text-danger' : ''}">${score.hole13}</td>
                                    <td class="${score.isWinner(14) ? 'bg-danger text-danger' : ''}">${score.hole14}</td>
                                    <td class="${score.isWinner(15) ? 'bg-danger text-danger' : ''}">${score.hole15}</td>
                                    <td class="${score.isWinner(16) ? 'bg-danger text-danger' : ''}">${score.hole16}</td>
                                    <td class="${score.isWinner(17) ? 'bg-danger text-danger' : ''}">${score.hole17}</td>
                                    <td class="${score.isWinner(18) ? 'bg-danger text-danger' : ''}">${score.hole18}</td>
                                    <td>${score.out}</td>
                                    <td>${score.total}</td>
                                    <td>${score.win}</td>
                                    <td><a href="#" id="remove" 
                                           onclick="document.getElementById('action').value = 'remove';document.getElementById('idScore').value = '${score.id}';                                                    
                                                    document.getElementById('scoresForm').submit();"> 
                                            <span class="glyphicon glyphicon-trash"/>
                                        </a>                                                   
                                    </td>
                                    <td><a id="edit" href="/score?idScore=${score.id}&searchAction=searchById">
                                    	<span class="glyphicon glyphicon-edit"/></a>
                                    </td>                                    
                                </tr>
                            </c:forEach>               
                        </table>  
                    </c:when>                    
                    <c:otherwise>
                        <br>           
                        <div class="alert alert-info">
                            No scorecard found matching your search criteria
                        </div>
                    </c:otherwise>
                </c:choose>                        
            </form>
            <form action ="jsp/new-scorecard.jsp">            
                <br></br>
                <button type="submit" class="btn btn-primary  btn-md">New Scorecard</button> 
            </form>
        </div>
    </body>
</html>