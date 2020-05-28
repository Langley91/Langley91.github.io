<?php

   session_start();
   include("_includes/config.inc");
   include("_includes/dbconnect.inc");
   include("_includes/functions.inc");

   // If the student has already been authenticated the $_SESSION['id'] variable
   // will been assigned their student id.

	// Escape special characters, if any
	 $txtid = mysqli_real_escape_string($conn, $_POST['txtid']);
	 $txtpwd = mysqli_real_escape_string($conn, $_POST['txtpwd']);


   // redirect to index if $_POST values not set or $_SESSION['id'] is already set
   if (!isset($txtid) || !isset($txtpwd) || isset($_SESSION['id'])) {
      header("Location: index.php");
	} else {
      if (validatelogin($txtid,$txtpwd) == true) {
         // valid
         header("Location: index.php?return=success");
      } else {
         // invalid
         unset($_SESSION['id']);
         header("Location: index.php?return=fail");
      }
	}
?>
