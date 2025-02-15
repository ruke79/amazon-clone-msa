import Item from "./Item";
import { profile } from "./profile";

const Sidebar = ({ data }) => {
    return (
        <div>
            <div className="flex flex-col items-center ">
                <img
                    src={data.image}
                    alt={data.username}
                    width={100}
                    height={100}
                    className="rounded-full outline outline-2 outline-offset-[3px] outline-slate-300"
                />
                <div className="mt-2 flex flex-col items-center">
                    <span className="font-bold text-xl">{data.username}</span>
                    <span className="text-sm text-slate-600">{data.email}</span>
                </div>
            </div>
            <ul className="mt-4">
                {profile.map((item, i) => (
                    <Item
                        key={i}
                        item={item}
                        visible={data.tab == i.toString()}
                        index={i.toString()}
                    />
                ))}
            </ul>
        </div>
    );
};

export default Sidebar;
