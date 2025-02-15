import { MinusCircleIcon, PlusCircleIcon } from "@heroicons/react/24/outline";
import { useState } from "react";

const sizeList = [
    "Mini",
    "Pro",
    "Max",
    "Max Pro",
    "Normal",
    "Small",
    "Medium",
    "Large",
    "Extra Large",
    "XS",
    "Full",
    "Queen",
    "King",
    "Split King",
    "Twin",
    "Twin XL",
    "S",
    "M",
    "L",
    "XL",
    "XXL",
    "3XL",
    "4XL",
    "5XL",
    "1",
    "2",
    "3",
    "4",
    "5",
    "6",
    "7",
    "8",
    "9",
    "10",
]


const Sizes = ({ sizes, product, setProduct }) => {
    const [noSize, setNoSize] = useState(false);

    const handleSize = (i, e) => {
        const values = [...sizes];
        values[i][e.target.name] = e.target.value;
        setProduct({
            ...product,
            sizes: values,
        });
    };
    const handleRemove = (i) => {
        if (sizes.length > 1) {
            const values = [...sizes];
            values.splice(i, 1);
            setProduct({
                ...product,
                sizes: values,
            });
        }
    };

    return (
        <div>
            <div className="my-2 pb-2 font-semibold border-b">
                Sizes / Quantity / Price
            </div>
            <button
                type="reset"
                className="px-4 py-1 bg-slate-200 rounded border border-slate-300 "
                onClick={() => {
                    if (!noSize) {
                        let data = sizes.map((size, i) => {
                            return {
                                quantity: size.quantity,
                                price: size.price,
                            };
                        });
                        
                        data.splice(1, data.length);
                        setProduct({ ...product, sizes: data });
                    } else {
                        let data = sizes.map((size) => {
                            return {
                                sizes: size.size || "",
                                quantity: size.quantity,
                                price: size.price,
                            };
                        });
                        setProduct({ ...product, sizes: data });
                    }
                    setNoSize((prev) => !prev);
                }}
            >
                {noSize
                    ? "click if product has size"
                    : "click if product has No size"}
            </button>
            {sizes
                ? sizes.map((size, i) => (
                      <div className="my-5 flex items-center" key={i}>
                          <select
                              name="size"
                              value={noSize ? "" : size.size}
                              disabled={noSize}
                              className={`border  rounded py-2 px-4 bg-slate-100 ${
                                  noSize ? "hidden" : ""
                              }`}
                              onChange={(e) => handleSize(i, e)}
                          >
                              <option value="">Select a size</option>
                              {sizeList.map((s) => (
                                  <option value={s} key={s}>
                                      {s}
                                  </option>
                              ))}
                          </select>
                          <input
                              type="number"
                              name="quantity"
                              placeholder={
                                  noSize ? "Product Quantity" : "size Quantity"
                              }
                              min={1}
                              value={size.quantity}
                              onChange={(e) => handleSize(i, e)}
                              className="ml-4 mr-2 border  rounded p-2 w-44  focus:bg-slate-200"
                          />
                          <input
                              type="number"
                              name="price"
                              placeholder={
                                  noSize ? "Product Price" : "size Price"
                              }
                              min={1}
                              value={size.price}
                              onChange={(e) => handleSize(i, e)}
                              className=" mx-2 border  rounded p-2 w-44  focus:bg-slate-200"
                          />
                          {!noSize ? (
                              <div className="flex">
                                  { sizes.length > 1 && (
                                    <MinusCircleIcon
                                      className="cursor-pointer w-8 h-8 hover:scale-[1.2] transition"
                                      onClick={() => handleRemove(i)}
                                  />
                                  )}
                                  <PlusCircleIcon
                                      className="cursor-pointer w-8 h-8 hover:scale-[1.2] transition"
                                      onClick={() =>
                                          setProduct({
                                              ...product,
                                              sizes: [
                                                  ...sizes,
                                                  {
                                                      size: "",
                                                      quantity: "",
                                                      price: "",
                                                  },
                                              ],
                                          })
                                      }
                                  />
                              </div>
                          ) : (
                              ""
                          )}
                      </div>
                  ))
                : ""}
        </div>
    );
};

export default Sizes;
