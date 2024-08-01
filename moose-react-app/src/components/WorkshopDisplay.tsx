import {WorkshopType} from "../ServerTypes";

interface WorkshopDisplayProps {
    workshop:WorkshopType,
    displayLink:boolean,
}

const WorkshopDisplay = (props: WorkshopDisplayProps) => {
    const { workshop, displayLink } = props;
    return (<div>
        {displayLink && <a href={"/workshop/" + workshop.id}><h2>{workshop.name}</h2></a>}
        {!displayLink && <h2>{workshop.name}</h2>}
        <p>{workshop.workshopStatusText}</p>


    </div>);

};
export default WorkshopDisplay;