import "react-responsive-carousel/lib/styles/carousel.min.css"; // requires a loader
import { Carousel } from "react-responsive-carousel";

import slider1 from "public/assets/images/slider-1.jpg";
import slider2 from "public/assets/images/slider-2.jpg";
import slider3 from "public/assets/images/slider-3.jpg";
import slider4 from "public/assets/images/slider-4.jpg";
import slider5 from "public/assets/images/slider-5.jpg";
import { ChevronLeftIcon, ChevronRightIcon } from "@heroicons/react/24/outline";
import { useLayoutEffect, useState } from "react";



const CarouselContainer = () => {
    const arrowStyles  = {
        position: "absolute",
        zIndex: 2,
        top: "calc(30% - 15px)",
        width: 50,
        height: 50,
        cursor: "pointer",
        filter: "drop-shadow(1px 3px 1px rgb(255 255 255 / 0.8))",
        color: "#404040",
    };
    
    let imgs = []
    const slidesSrc = [ slider1, slider2, slider3, slider4, slider5 ];
    const [preloadedimgs, setPreloadedimgs] = useState([]);

        
    
    const preloadSlides= () => {
        if (preloadedimgs.length ==0 ) {        
            for(let i = 0; i < slidesSrc.length; i++)  {
                
                imgs[i] = new Image();
                imgs[i].src = slidesSrc[i];
            }
            setPreloadedimgs(imgs);            
        }
    }            

    const renderimgs = preloadedimgs.map((img, i) => {                               
                         return (
                         <div key={"slider" + i}>
                         <img src={img.src} alt={"slider" + i} />
                        </div>
                         )
                     });

    useLayoutEffect(()=>{
        
        preloadSlides();        
    },[slidesSrc]);

    return (
        <>
            <Carousel
                renderArrowPrev={(onClickHandler, hasPrev, label) =>
                    hasPrev && (
                        <button
                            className=""
                            type="button"
                            onClick={onClickHandler}
                            title={label}
                            style={{ ...arrowStyles, left: 15 }}
                        >
                            <ChevronLeftIcon />
                        </button>
                    )
                }
                renderArrowNext={(onClickHandler, hasNext, label) =>
                    hasNext && (
                        <button
                            type="button"
                            onClick={onClickHandler}
                            title={label}
                            style={{ ...arrowStyles, right: 15 }}
                        >
                            <ChevronRightIcon />
                        </button>
                    )
                }
                showStatus={false}
                showArrows={true}
                infiniteLoop={true}
                emulateTouch={true}
                autoPlay={true}
                showIndicators={false}
                showThumbs={false}               
            >
                 {       
              renderimgs                                        
                 }             
            
            </Carousel>             
        </>
    );
};

export default CarouselContainer;
