import DialogModal from "components/DialogModal";
import { useDispatch } from "react-redux";
import { showDialog } from "../../../redux/DialogSlice";
import { uploadImages } from "util/uploadImages";
import dataURItoBlob from "util/dataURItoBlob";
import { Rating } from "@mui/material";
import api from 'util/api';
import { useState } from "react";
import ImagesReview from "./Images";
import Select from "./Select";

let fits = ["Small", "True to size", "Large"];

const AddReview = ({ product, setReviews }) => {
    const dispatch = useDispatch();
    const [loading, setLoading] = useState(false);
    const [size, setSize] = useState("");
    const [style, setStyle] = useState("");
    const [fit, setFit] = useState("");
    const [review, setReview] = useState("");
    const [rating, setRating] = useState(0);
    const [images, setImages] = useState([]);
    let uploaded_images = [];

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
        if (images.length > 0) {
            let temp = [];
            if (images.length > 0) {
                temp = images.map((img) => dataURItoBlob(img));
            }
            const path = "review images";
            let formData = new FormData();
            formData.append("path", path);
            temp.forEach((img) => {
                formData.append("file", img);
            });
            uploaded_images = await uploadImages(formData);
            console.log(uploaded_images);
        } else {
            const { data } = await api.put(
                `/product/${product.product_id}/review`,
                {
                    size,
                    style,
                    fit,
                    rating,
                    review,
                    images: uploaded_images,
                }
            );
            setReviews(data.reviews);
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
        }
    };

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
            </div>
        </div>
    );
};

export default AddReview;
