<workforce>
{
for $empName in distinct-values(projects/project//name)
order by $empName
return
	<emp name='{$empName}' roles='{let $rolesPlayed := for $a in projects
		return if(exists($a//leader[name = $empName]) and exists($a//analyst[name = $empName]))
	        		then "leader,analyst"
	               
			else if(exists($a//analyst[name = $empName]) and not(exists($a//leader[name = $empName])))
	          		then "analyst"
	         
			else if(exists($a//leader[name = $empName]) and not(exists($a//analyst[name = $empName])))
				then "leader"
			else()
	
		return $rolesPlayed}'>
		<projects>
		{for $b in projects/project 
			return 
				if($b//name= $empName) then <proj>{data($b/@pid)}</proj> 
				else()
		}
		</projects>
	</emp>
}
</workforce>
