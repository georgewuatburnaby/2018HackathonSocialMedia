import { Component } from '@angular/core';
import { BackService } from './back.service'


@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.css']
})

export class AppComponent {
  title = 'app';
  raw_data : HModel[] = []
  data : HModel[] = []
  searchTerm: string = ''
  filter_d = -100
  filter_t = 'all'

  // [{
  //   class: 1,
  //   text:'Activations that are more complex than a simple TensorFlow/Theano/CNTK function (eg. learnable activations)',
  //   tags:['Keras','Machine Learning'],
  //   url:'http://www.google.com',
  //   source: 'Twitter'
  // },{
  //   class: 0,
  //   text:'A bad Review',
  //   tags:['Demo','Test'],
  //   url:'http://www.google.com',
  //   source: 'Reddit'
  // }]

  constructor(private backService: BackService) { 
    console.log(backService.get(null))
  }
  ngOnInit() {
    this.fetch()
    // setInterval(()=> this.fetch(),5000)
  }

  search(){
    if(this.searchTerm !== ''){
      console.log('doing:', this.searchTerm.toLowerCase())
    }
  }

  filter_result(n:number = -200,t:string='o'):void{
    console.log('here',n,this.raw_data.length)
    console.log(this.filter_d,this.filter_t)
    if(n === -200){
      n = this.filter_d
    }else{
      this.filter_d = n
    }
    if(t === 'o'){
      t = this.filter_t
    }else{
      this.filter_t = t
    }
    console.log(n,t)
    let tmp = this.raw_data.slice(0).filter(data => {
      // console.log(data.text,data.text.length,data.text.length>40,data.class,data.rating)
      return data.text.length>40
    })
    console.log(tmp.length,typeof(n))
    if(n != -100){
      tmp = tmp.filter(data => data.class === n)
    }
    if(t !='all'){
      tmp = tmp.filter(data => data.source == t)
    }
    console.log(tmp.length,this.filter_d,this.filter_t)
    this.data = tmp.sort((o1, o2) => o1.timeStamp.getSeconds() - o2.timeStamp.getSeconds())
  }

  private fetch():void{
    try{
      this.backService.get(null).subscribe(data =>{
        const cdata = data['_embedded']['comments']
        this.raw_data = cdata.map(c =>{
          let r:number = 0
          if (c['rating'] > .8){
            r = 1
          }else if(c['rating'] < .2){
            r = -1
          }
          var d = new Date(0); // The 0 there is the key, which sets the date to the epoch
          d.setUTCSeconds(c['timeStamp']);
          return {
            class: r,
            text: c['text'],
            tags: c['tags'],
            timeStamp: d,
            source: c['source'],
            url: c['url'],
            rating: c['rating']
          }
        })
        this.filter_result()
      })
    }catch(e){

    }
  }
 
}

export interface HModel{
  class:number
  url?:string
  source?:string
  tags?: string[]
  text:string
  timeStamp?:Date
  rating?:number
}
