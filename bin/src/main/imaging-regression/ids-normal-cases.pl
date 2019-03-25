#!/usr/bin/perl

# This script tests the RAD-55 response of the Imaging Document Source simulator.
# It looks through all of the files stored in the image cache for a simulator
# and performs a WADO retrieve for each one. This is a test of the success path.

use strict;
use File::Find;
use LWP::UserAgent();

sub extract {
 my ($response) = @_;
 return ($response->code, $response->status_line, $response->header("content_type"));
}

sub map_file_to_mime_type {
 my ($file_name) = @_;

 my %h = (
  "1.2.840.10008.1.2.1"    => "application/dicom",
  "1.2.840.10008.1.2.4.51" => "application/dicom",
  "1.2.840.10008.1.2.4.70" => "application/dicom",
  "export.jpg"             => "image/jpeg"
 );

 my $mime_type = $h{$file_name};

 die "Could not map this file name to a mime type: $file_name" if ($mime_type eq "");

 return $mime_type;
}

sub wado_retrieve {
 my ($line, $base, $output) = @_;
 return if (substr($line, 0, 1) eq "#");

 my $ua = LWP::UserAgent->new;
 $ua->timeout(180);
# $ua->show_progress(1);

 my ($study, $series, $instance, $file) = split /\//, $line;
 my $accept = map_file_to_mime_type($file);

 my $url = $base . "?requestType=WADO&contentType=$accept&studyUID=$study&seriesUID=$series&objectUID=$instance";

 my $response = $ua->get($url, "Accept" => "$accept", ":content_file" => "$output");

 my ($rtn_status, $rtn_string) = check_response($response, $accept);
 return ($rtn_status, $rtn_string, $url);
}

sub check_response {
 my ($response, $accept) = @_;

 my $rtn_status = 0;
 my $rtn_string = "";
 my ($code, $status_line, $rtn_content_type) = extract($response);

 if (! $response->is_success) {
  $rtn_status = 1;
  $rtn_string .= "<$status_line>";
 }
 if ($code ne "200") {
  $rtn_status = 1;
  $rtn_string .= "<HTTP response code: $code>";
  $rtn_string .= "<$status_line>";
 }
 if ($accept ne $rtn_content_type) {
  $rtn_status = 1;
  $rtn_string .= "<Content type in return header ($rtn_content_type) does not match requested accept type ($accept)>";
  $rtn_string .= "<$status_line>";
 }
 return ($rtn_status, $rtn_string);
}

sub compare_file_content {
 my ($orig_file, $retrieve_file) = @_;
 my $rtn_status = 0;
 my $rtn_string = "";

 my $x = "cmp -s $orig_file $retrieve_file";
 my $y = `$x`;
 if ($?) {
  $rtn_status = 1;
  $rtn_string = "Retrieved file $retrieve_file does not match original file $orig_file";
 }
 return ($rtn_status, $rtn_string);
}

sub process_file {
 my ($root, $path, $index, $base) = @_;

 my $root_length = length $root;
 my $short_path  = substr($path, $root_length+1);
 my ($status, $status_string, $url) = wado_retrieve($short_path, $base, "/tmp/abc");
 if ($status eq "0") {
  ($status, $status_string) = compare_file_content($path, "/tmp/abc");
 }
 if ($status ne "0") {
  print "ERROR: $index, $root, $path\n" .
        "ERROR: <$status_string>\n" .
        "ERROR: <$url>\n";

  print STDERR
	"ERROR: $index, $root, $path\n" .
        "ERROR: <$status_string>\n" .
        "ERROR: <$url>\n";
 }
 return ($status, $status_string);
}

sub read_paths {
 my $path = shift;
 find({ wanted => \&process_path, no_chdir => 1}, $path);
}

sub process_path {
 if (-f $_) {
  push (@main::paths, $_);
 }
}

sub check_args {
 my $arg_count = scalar(@_);
 die "Arguments: <folder> <base URL>\n" .
     "           folder:   Simulator folder\n" .
     "           base URL: URL used for WADO retrieves\n"

 if ($arg_count != 2);
}

check_args(@ARGV);

my @paths;
my ($root, $base) = @ARGV;
read_paths($root);

my $index = 0;
my $file_count = scalar(@main::paths);
my $failed_operations = 0;
print STDERR "Base URL: $base\n";
print STDERR "Found $file_count files to process\n";

foreach my $file (@main::paths) {
 $index++;
 print STDERR "Normal path, processing file $index of $file_count\n";

 my ($status, $status_string) = process_file($root, $file, $index, $base);
 print "Index: $index Status: $status File: $file\n";
 $failed_operations++ if ($status != 0);
}

print STDERR "\nBase URL: $base\n";
print STDERR "Total files: $file_count, Failed operations: $failed_operations\n";

print "\nBase URL: $base\n";
print "Total files: $file_count, Failed operations: $failed_operations\n";
