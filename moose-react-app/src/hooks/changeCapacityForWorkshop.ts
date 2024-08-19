import {AdminWorkshopType, ChangeCapacityType} from "../ServerTypes";
import ServerConfig from "../ServerConfig";

const changeCapacityForWorkshop = (changeCapacityType:ChangeCapacityType):Promise<AdminWorkshopType> => {
    return new Promise((resolve, reject) => {
        fetch(ServerConfig.address + "/api/admin/changeCapacity", {
            method: "POST",
            headers: {
                "Content-Type": "application/json"
            },
            body: JSON.stringify(changeCapacityType)
        })
            .then(response => {
                if (response.ok) {
                    return response.json();
                } else {
                    return response.text().then(errorText => {
                        throw new Error(errorText);
                    });

                }
            })
            .then(json => {
                resolve(json);
            })
            .catch(error => {
                reject(error.message);
            });
    });
}

export default changeCapacityForWorkshop;