/*
 *	Title: detector.h	
 *	Written by: Jamie Miller
 *	Team: Luminous Moss Boss
 *	------------------------
 *	Description: Static class for detection algorithms.
 */
 
#ifndef __DETECTOR__

#ifdef FORIPHONE
  #import <opencv2/opencv.hpp>
#else
  #include <opencv/cv.h>
#endif
#include <opencv2/core/core.hpp>
using namespace cv;

#ifndef __IDENTIFIED__
  #include "identified.h"
#endif

class detector {
  private:
    CascadeClassifier flower_cascade;
  public:
    detector(String flower_xml_name);
    Mat isolatePink(Mat image);
    vector<identified> findFlowers(Mat image);
    Mat circlePinkFlowers(Mat image);
    bool isThisSilene(Mat image);
};

#endif
