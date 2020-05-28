<?php

   include("_includes/config.inc");
   include("_includes/dbconnect.inc");
   include("_includes/functions.inc");

	//delete multiple code
	if(isset($_POST['delete']))
	{
	 $cnt=array();
	 $cnt=count($_POST['del']);
	 for($i=0;$i<$cnt;$i++)
	  {
		 $del_id=$_POST['del'][$i];
		 $query="delete from student where studentid=".$del_id;
		 mysqli_query($conn,$query);
	  }
	}
   // check logged in
   if (isset($_SESSION['id'])) {

      echo template("templates/partials/header.php");
      echo template("templates/partials/nav.php");

      // Build SQL statment that selects a student's modules
      $sql = "select * from student";

      $result = mysqli_query($conn,$sql);

      // prepare page content
      $data['content'] .= "<form name='delstudents' method='post' action=''><table class='table table-hover'>";
      $data['content'] .= "<tr><th colspan='11' align='center'>Students</th></tr>";
      $data['content'] .= "<tr><th></th><th>studentid</th><th>password</th><th>dob</th><th>firstname</th><th>lastname</th><th>house</th><th>town</th><th>county</th><th>country</th><th>postcode</th></tr>";
      // Display the modules within the html table
      while($row = mysqli_fetch_array($result)) {
         $data['content'] .= "<tr><td><input type='checkbox' name='del[]' value=$row[studentid] ></td><td> $row[studentid] </td><td> $row[password] </td><td> $row[dob] </td><td> $row[firstname] </td><td> $row[lastname] </td><td> $row[house] </td><td> $row[town] </td><td> $row[county] </td><td> $row[country] </td><td> $row[postcode] </td></tr>";
      }
	  $data['content'] .= "<tr><td colspan='11'><input name='delete' type='submit' value='Delete' class='btn btn-primary btn-form'></td></tr>";
      $data['content'] .= "</table></form>";

      // render the template
      echo template("templates/default.php", $data);

   } else {
      header("Location: index.php");
   }

   echo template("templates/partials/footer.php");

?>
