import DialogModal from "components/DialogModal";
import { useDispatch } from "react-redux";
import { showDialog } from "../../../redux/DialogSlice";
import { uploadImages } from "util/imageUtil";
import dataURItoBlob from "util/dataURItoBlob";
import { Rating } from "@mui/material";
import api, { putRequest, deleteRequest } from 'util/api';
import { useState } from "react";
import ImagesReview from "./Images";
import Select from "./Select";
import axios from "axios";
import { useMutation } from "@tanstack/react-query";
import { useErrorBoundary } from "react-error-boundary";

let fits = ["Small", "True to size", "Large"];

const deleteReview = async (productId) => {
    const { data } = await deleteRequest(
        `user-service/api/review/delete/${productId}`
    );
    return data;
}

const addReview = async ({ productId, formData }) => {
    
    const { data } = await putRequest(
        `user-service/api/review/${productId}/add`, formData,
        {
            headers: {
                "Content-Type": "multipart/form-data",

            },
            transformRequest: [
                function () {
                    return formData;
                },
            ],
        }
    );
    return data;
}



const AddReview = ({ product, setReviews }) => {
    const dispatch = useDispatch();
    const [loading, setLoading] = useState(false);
    const [size, setSize] = useState("");
    const [style, setStyle] = useState("");
    const [fit, setFit] = useState("");
    const [review, setReview] = useState("");
    const [rating, setRating] = useState(0);
    const [images, setImages] = useState([]);

    const showBoundary = useErrorBoundary();


    const { mutate: addReviewOp } = useMutation({
        mutationFn: addReview,
        onSuccess: (response) => {

            setReviews(response);
            dispatch(
                showDialog({
                    header: "Adding review Successfully!",
                    msgs: [{
                        msg: "Adding review Successfully.",
                        type: "success",
                    }],
                })
            );
            setSize("");
            setStyle("");
            setFit("");
            setRating(0);
            setImages([]);
            setReview("");
            setLoading(false);


        },
        onError: (error) => {
            showBoundary(error);
        }
    });

    const { mutate: deleteReviewOp } = useMutation({
        mutationFn: deleteReview,
        onSuccess: (response) => {
            setReviews(null);
            dispatch(
                showDialog({
                    header: "Delete review Successfully!",
                    msgs: [{
                        msg: "Delete review Successfully.",
                        type: "success",
                    }],
                })
            );
        },
        onError: (error) => {
            showBoundary(error);
        }
    });

    const handleSubmit = async () => {
        setLoading(true);
        let msgs = [];
        if (!size) {
            msgs.push({
                msg: "Please select a size!",
                type: "error",
            });
        }
        if (!style) {
            msgs.push({
                msg: "Please select a style!",
                type: "error",
            });
        }
        if (!fit) {
            msgs.push({
                msg: "Please select a Fit!",
                type: "error",
            });
        }
        if (rating < 0.4) {
            msgs.push({
                msg: "Please select a rating!",
                type: "error",
            });
        }
        if (!review) {
            msgs.push({
                msg: "Please add a review!",
                type: "error",
            });
        }
        if (msgs.length > 0) {
            dispatch(
                showDialog({
                    header: "Adding review error!",
                    msgs,
                })
            );
            setLoading(false);
            return;
        }

        let reviewData = {
            size,
            style,
            fit,
            rating,
            review,                
        };

        let formData = new FormData();        
        formData.append("review", new Blob([JSON.stringify(reviewData)], {
            type: 'application/json'
        }));

        if (images.length > 0) {

            let files = images.map((img) => {
                return dataURItoBlob(img);
            });

            // const path = "review images";
            // let imageUploader = files.map(async (file) => {
            //     let formData = new FormData();
            //     formData.append("path", path);
            //     formData.append("file", file);
            //     formData.append("upload_preset", "nd7idl8b");
            //     formData.append("api_key", process.env.REACT_APP_CLOUDINARY_KEY);
            //     formData.append("timestamp", (Date.now() / 1000) | 0);


            //     const image = await uploadImages(formData);
            //     uploaded_images.push(image.url);
            // });
            
            files.map((file) => {
                formData.append("image", file);
            })

            addReviewOp({productId: product.id, formData:formData});

        } else {

            addReviewOp({ productId: product.id, formData:formData});
        }
    }

    const handleDeleteReivew = async () => {

        deleteReviewOp(product.id);
    }

    return (
        <div className="flex flex-col">
            <DialogModal />
            <div className="flex flex-wrap max-md:space-y-4 mt-2">
                <Select
                    property={size}
                    text="size"
                    data={product.allSizes.filter(
                        (item) => item.size !== size
                    )}
                    handleChange={setSize}
                />
                <Select
                    property={style}
                    text="style"
                    data={product.colors.filter((item) => item !== style)}
                    handleChange={setStyle}
                />
                <Select
                    property={fit}
                    text="fit"
                    data={fits.filter((item) => item !== fit)}
                    handleChange={setFit}
                />
            </div>
            <ImagesReview images={images} setImages={setImages} />
            <textarea
                className="mt-4 p-3 w-full rounded-md bg-white h-[200px] border border-slate-200 outline-none resize-none"
                name="review"
                value={review}
                onChange={(e) => setReview(e.target.value)}
                placeholder="Write you review here..."
            ></textarea>
            <div className="flex flex-col md:flex-row items-center md:space-x-10">
                <Rating
                    className="mt-4"
                    name="half-rating-read"
                    defaultValue={0}
                    value={rating}
                    precision={0.5}
                    onChange={(e) => setRating(e.target.value)}
                    style={{ color: "#FACF19", fontSize: "3rem" }}
                />
                <button
                    disabled={loading}
                    onClick={() => handleSubmit()}
                    className={`w-full mt-4  p-3  font-semibold rounded-md transition-all ${loading ? 'bg-gradient-to-r from-amazon-blue_light to-slate-400 text-white cursor-not-allowed' : 'bg-gradient-to-r from-amazon-orange to-yellow-300 text-amazon-blue_dark hover:scale-95'}`}
                >
                    {loading ? 'loading ...' : 'Submit Review'}
                </button>
                <button
                    disabled={loading}
                    onClick={() => handleDeleteReivew()}
                    className={`w-full mt-4  p-3  font-semibold rounded-md transition-all ${loading ? 'bg-gradient-to-r from-amazon-blue_light to-slate-400 text-white cursor-not-allowed' : 'bg-gradient-to-r from-amazon-orange to-yellow-300 text-amazon-blue_dark hover:scale-95'}`}
                >
                    Delete Review
                </button>

            </div>
        </div>
    );
};

export default AddReview;
