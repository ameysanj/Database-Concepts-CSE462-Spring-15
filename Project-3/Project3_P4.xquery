for $x in /projects/project
return if (not(exists($x/analyst)))
then <noanalysts><pid>{data($x/@pid)}</pid></noanalysts>
else ()
