<?php

include("_includes/config.inc");
include("_includes/dbconnect.inc");
include("_includes/functions.inc");


// check logged in
if (isset($_SESSION['id'])) {

   echo template("templates/partials/header.php");
   echo template("templates/partials/nav.php");

   // if the form has been submitted
   if (isset($_POST['submit'])) {

	   	   // Escape special characters, if any
	   $firstname =  mysqli_real_escape_string($conn, $_POST['txtfirstname']);
	   $lastname =  mysqli_real_escape_string($conn, $_POST['txtlastname']);
	   $house =   mysqli_real_escape_string($conn, $_POST['txthouse']);
	   $town =  mysqli_real_escape_string($conn, $_POST['txttown']);
	   $county =  mysqli_real_escape_string($conn, $_POST['txtcounty']);
	   $country =  mysqli_real_escape_string($conn, $_POST['txtcountry']);
	   $postcode =  mysqli_real_escape_string($conn, $_POST['txtpostcode']);
	   
      // build an sql statment to update the student details
      $sql = "update student set firstname ='" . $firstname . "',";
      $sql .= "lastname ='" . $lastname  . "',";
      $sql .= "house ='" . $house  . "',";
      $sql .= "town ='" . $town  . "',";
      $sql .= "county ='" . $county  . "',";
      $sql .= "country ='" . $country  . "',";
      $sql .= "postcode ='" . $postcode  . "' ";
      $sql .= "where studentid = '" . $_SESSION['id'] . "';";
      $result = mysqli_query($conn,$sql);

      $data['content'] = "<div class='alert alert-success'>Your details have been updated</div>";

   }
   else {
      // Build a SQL statment to return the student record with the id that
      // matches that of the session variable.
      $sql = "select * from student where studentid='". $_SESSION['id'] . "';";
      $result = mysqli_query($conn,$sql);
      $row = mysqli_fetch_array($result);

      // using <<<EOD notation to allow building of a multi-line string
      // see http://stackoverflow.com/questions/6924193/what-is-the-use-of-eod-in-php for info
      // also http://stackoverflow.com/questions/8280360/formatting-an-array-value-inside-a-heredoc
      $data['content'] = <<<EOD

   <h2>My Details</h2>
   <form name="frmdetails" action="" method="post">
   First Name :
   <input name="txtfirstname" class='form-control'  type="text" value="{$row['firstname']}" /><br/>
   Surname :
   <input name="txtlastname" class='form-control'  type="text"  value="{$row['lastname']}" /><br/>
   Number and Street :
   <input name="txthouse" class='form-control'  type="text"  value="{$row['house']}" /><br/>
   Town :
   <input name="txttown" class='form-control'  type="text"  value="{$row['town']}" /><br/>
   County :
   <input name="txtcounty" class='form-control'  type="text"  value="{$row['county']}" /><br/>
   Country :
   <input name="txtcountry" class='form-control'  type="text"  value="{$row['country']}" /><br/>
   Postcode :
   <input name="txtpostcode" class='form-control'  type="text"  value="{$row['postcode']}" /><br/>
   <input type="submit" value="Save" class='btn btn-primary btn-form btn-block' name="submit"/>
   </form>

EOD;

   }

   // render the template
   echo template("templates/default.php", $data);

} else {
   header("Location: index.php");
}

echo template("templates/partials/footer.php");

?>
